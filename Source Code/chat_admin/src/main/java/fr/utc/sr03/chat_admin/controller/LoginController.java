package fr.utc.sr03.chat_admin.controller;

import fr.utc.sr03.chat_admin.controller_rest.LoginApiController;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LoginApiController loginApiController;  // Injecter le contrôleur API

    @GetMapping
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping
    public String postLogin(@ModelAttribute User user, Model model, HttpSession session, HttpServletResponse response) {
        ResponseEntity<Map<String, String>> apiResponse = loginApiController.postLogin(user, session);
        Map<String, String> responseBody = apiResponse.getBody();

        if (responseBody.containsKey("token")) {
            String token = responseBody.get("token");

            // Stocker le jeton JWT dans un cookie
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setHttpOnly(true); // Empêcher l'accès aux scripts côté client
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtTokenProvider.getValidityInMilliseconds() / 1000));
            response.addCookie(cookie);

            // connexion réussie
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser != null && loggedUser.getAdmin()) {
                return "redirect:/admin/accueil";
            } else {
                model.addAttribute("error", "You are not admin");
                return "login";
            }
        } else {
            // connexion échoue
            model.addAttribute("error", responseBody.get("error"));
            return "login";
        }
    }

}
