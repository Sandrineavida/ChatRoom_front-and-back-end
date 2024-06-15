package fr.utc.sr03.chat_admin.controller;

import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAddUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        return "add_user"; // Assuming your Thymeleaf template is named "add_user.html"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addd")
    public String addUser(@ModelAttribute User user, ModelMap model) {
        if (userService.isMailExists(user.getMail())) {
            model.addAttribute("error", "Email already exists");
            return "add_user";
        } else if (!userService.validatePassword(user.getPassword())) {
            model.addAttribute("error", "Password must be at least 8 characters long, and contain upper and lower case letters, digits, and special characters");
            return "add_user";
        } else {
            userService.saveUser(user);
            model.addAttribute("success", "User successfully created. Notify " + user.getMail() + " and send the login password.");
            return "add_user";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{userMail}")
    public String deleteUser(@PathVariable String userMail) {
        userService.deleteUser(userMail);
        return "redirect:/admin/users"; // After deletion, redirect to the user list
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/toggleLock/{userMail}")
    public String toggleUserLock(@PathVariable String userMail) {
        userService.toggleUserLock(userMail);
        return "redirect:/admin/users"; // After toggling lock, redirect to the user list
    }

    @GetMapping("/login")
    public String showUser(ModelMap model) {
        return "user_show";
    }
}
