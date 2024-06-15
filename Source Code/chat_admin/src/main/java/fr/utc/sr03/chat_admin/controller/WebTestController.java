package fr.utc.sr03.chat_admin.controller;

import fr.utc.sr03.chat_admin.repository.UserRepository;
import fr.utc.sr03.chat_admin.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * URL de base du endpoint : http://localhost:8080/web/test
 */
@Controller
@RequestMapping("web/test")
public class WebTestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebTestController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseBody // Pour faire sans template html
    public String testUserRepository() {
        LOGGER.info("=== ALL USERS ===");
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            LOGGER.info("[" + user.getId() + "]" + user.getFirstName() + " : " + user.getAdmin());
        });

        return "OK";
    }
}