package fr.utc.sr03.chat_admin.controller_rest;

import fr.utc.sr03.chat_admin.dto.UserDTO;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/all-for-login")
    public ResponseEntity<List<UserDTO>> getAllUsersForLogin() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getMail(), user.getAdmin()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}
