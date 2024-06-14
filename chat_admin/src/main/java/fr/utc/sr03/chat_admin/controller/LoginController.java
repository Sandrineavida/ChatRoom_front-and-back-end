//package fr.utc.sr03.chat_admin.controller;
//
//import fr.utc.sr03.chat_admin.model.User;
//import fr.utc.sr03.chat_admin.service.UserService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("login")
//public class LoginController {
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public String getLogin(Model model) {
//        model.addAttribute("user", new User());
//        return "login";
//    }
//
//    @PostMapping
//    public String postLogin(@ModelAttribute User user, Model model, HttpSession session) {
//        String email = user.getMail();
//        String password = user.getPassword();
//
//        // 检查邮箱是否存在
//        if (!userService.isMailExists(email)) {
//            model.addAttribute("error", "Email does not exist");
//            return "login";
//        }
//
//        // 检查用户是否被锁定
//        if (user.isLocked()) {
//            model.addAttribute("error", "Account is locked due to multiple failed login attempts.");
//            return "login";
//        }
//        System.out.println(email+ "Locked?: "+user.isLocked());
//
//        // 检查密码格式
//        if (!userService.validatePassword(password)) {
//            model.addAttribute("error", "Invalid password format !");
//            return "login";
//        }
//
//        User loggedUser = userService.authenticate(email, password);
//
//        if (loggedUser != null) {
//            session.setAttribute("loggedUser", loggedUser);
//            // 登录成功
//            if (loggedUser.getAdmin()) {
//                return "redirect:/admin/accueil";
//            } else {
//                return "redirect:/users/login";
//            }
//        } else {
//            // 登录失败
//            int remainingAttempts = userService.getRemainingAttempts(email);
//            if (remainingAttempts == 0) {
//                model.addAttribute("error", "Account locked due to multiple failed login attempts.");
//            } else {
//                model.addAttribute("error", "Invalid credentials. You have " + remainingAttempts + " attempts remaining.");
//            }
//            return "login";
//        }
//    }
//}

package fr.utc.sr03.chat_admin.controller;

import fr.utc.sr03.chat_admin.controller_rest.LoginApiController;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.security.JwtTokenProvider;
import fr.utc.sr03.chat_admin.service.UserService;
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
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LoginApiController loginApiController;  // 注入API控制器

    @GetMapping
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

//    @PostMapping
//    public String postLogin(@ModelAttribute User user, Model model, HttpSession session) {
//        ResponseEntity<Map<String, String>> apiResponse = loginApiController.postLogin(user, session);
//        Map<String, String> response = apiResponse.getBody();
//
//        if (response.containsKey("token")) {
//            System.out.println(response);
//            // 登录成功
//            User loggedUser = (User) session.getAttribute("loggedUser");
//            if (loggedUser != null && loggedUser.getAdmin()) {
//                return "redirect:/admin/accueil";
//            } else {
//                return "redirect:/users/login";
//            }
//        } else {
//            // 登录失败
//            model.addAttribute("error", response.get("error"));
//            return "login";
//        }
//    }
    @PostMapping
    public String postLogin(@ModelAttribute User user, Model model, HttpSession session, HttpServletResponse response) {
        ResponseEntity<Map<String, String>> apiResponse = loginApiController.postLogin(user, session);
        Map<String, String> responseBody = apiResponse.getBody();

        if (responseBody.containsKey("token")) {
            String token = responseBody.get("token");

            // 将JWT令牌存储到Cookie中
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setHttpOnly(true); // 防止客户端脚本访问
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtTokenProvider.getValidityInMilliseconds() / 1000)); // 设置Cookie的有效期
            response.addCookie(cookie);

            // 登录成功
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser != null && loggedUser.getAdmin()) {
                return "redirect:/admin/accueil";
            } else {
                model.addAttribute("error", "You are not admin");
                return "login";
            }
        } else {
            // 登录失败
            model.addAttribute("error", responseBody.get("error"));
            return "login";
        }
    }

}
