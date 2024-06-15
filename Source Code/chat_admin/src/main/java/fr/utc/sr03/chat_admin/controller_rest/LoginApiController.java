package fr.utc.sr03.chat_admin.controller_rest;

import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.security.JwtTokenProvider;
import fr.utc.sr03.chat_admin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins="*", allowedHeaders="*")
public class LoginApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Map<String, String>> postLogin(@RequestBody User user, HttpSession session) {
        String email = user.getMail();
        String password = user.getPassword();

        Map<String, String> response = new HashMap<>();

        // Vérifiez si la boîte aux lettres existe
        if (!userService.isMailExists(email)) {
            response.put("error", "Email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérifier si l'utilisateur est verrouillé
        if (user.isLocked()) {
            response.put("error", "Account is locked due to multiple failed login attempts.");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérifier le format du mot de passe
        if (!userService.validatePassword(password)) {
            response.put("error", "Invalid password format !");
            return ResponseEntity.badRequest().body(response);
        }

        User loggedUser = userService.authenticate(email, password);

        if (loggedUser != null) {
            session.setAttribute("loggedUser", loggedUser);
            String token = jwtTokenProvider.createSimpleToken(loggedUser.getMail(), loggedUser.getAdmin() ? "ROLE_ADMIN" : "ROLE_USER");

            response.put("id", Long.toString(loggedUser.getId()));
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            int remainingAttempts = userService.getRemainingAttempts(email);
            String errorMessage = remainingAttempts == 0 ? "Account locked due to multiple failed login attempts." : "Invalid credentials. You have " + remainingAttempts + " attempts remaining.";
            response.put("error", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
