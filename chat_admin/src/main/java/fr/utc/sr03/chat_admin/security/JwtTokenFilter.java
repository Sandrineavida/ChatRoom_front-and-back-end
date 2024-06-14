//package fr.utc.sr03.chat_admin.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);
//
//    private static final String BEARER_PREFIX = "Bearer ";
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Optional<String> token = extractToken(request);
//
//        if (token.isPresent()) {
//            Optional<Authentication> authentication = jwtTokenProvider.getAuthentication(token.get());
//            if (authentication.isPresent()) {
//                LOGGER.info("Authentication: " + authentication.get());
//                SecurityContextHolder.getContext().setAuthentication(authentication.get());
//            }
//            else{
//                LOGGER.info("Authentication failed. Clear context");
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private Optional<String> extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
//            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
//        }
//
//        return Optional.empty();
//    }
//}

package fr.utc.sr03.chat_admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = extractToken(request);

        if (token.isPresent()&& jwtTokenProvider.isTokenValid(token.get())) {
            Optional<Authentication> authentication = jwtTokenProvider.getAuthentication(token.get());
            if (authentication.isPresent()) {
                LOGGER.info("Authentication: " + authentication.get());
                SecurityContextHolder.getContext().setAuthentication(authentication.get());
            } else {
                LOGGER.info("Authentication failed. Clear context");
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        // 从Cookie中提取JWT令牌
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwtToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
        }

        // 从Authorization头部提取JWT令牌
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }

        return Optional.empty();
    }
}
