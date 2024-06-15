package fr.utc.sr03.chat_admin.controller;

import fr.utc.sr03.chat_admin.repository.UserRepository;
import fr.utc.sr03.chat_admin.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("accueil")
    public String adminAccueil(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            User admin = (User) session.getAttribute("loggedUser");
            model.addAttribute("admin", admin);
            return "admin";
        }
        return "redirect:/login?error";
    }

    @GetMapping("users")
    public String getUserList(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "lastName") String sortBy,
                              @RequestParam(defaultValue = "") String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> userPage;

        if (search.isEmpty()) {
            userPage = userRepository.findAll(pageRequest);
        } else {
            userPage = userRepository.findByMailContainingIgnoreCase(search, pageRequest);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("search", search);

        return "user_list";
    }
}
