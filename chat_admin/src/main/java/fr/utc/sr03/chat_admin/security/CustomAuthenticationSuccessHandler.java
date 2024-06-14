//package fr.utc.sr03.chat_admin.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        // 获取当前登录用户的角色
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
//
//        if (isAdmin) {
//            response.sendRedirect("/admin/accueil");
//        } else {
//            response.sendRedirect("/users/login");
//        }
//    }
//}
