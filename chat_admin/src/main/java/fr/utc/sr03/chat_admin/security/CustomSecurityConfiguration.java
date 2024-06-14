package fr.utc.sr03.chat_admin.security;

import fr.utc.sr03.chat_admin.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//
//@Configuration
//@EnableWebSecurity
//public class CustomSecurityConfiguration {
//    private final JwtTokenFilter jwtTokenFilter;
//
//    public CustomSecurityConfiguration(JwtTokenFilter jwtTokenFilter) {
//        this.jwtTokenFilter = jwtTokenFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // Configuration des autorisations par requetes
//                .authorizeHttpRequests((authz) -> authz
//                        // Autorisation des ressources "statiques"
//                        .requestMatchers("/html/css/**", "/html/js/**").permitAll()
////                        .requestMatchers("/login/**", "/admin/**", "/web/test").permitAll()
//                        // Autorisation du endpoint "test" pour la demo
//                        .requestMatchers("/api/open/test/**").permitAll()
//                        // Autorisation du endpoint REST "login" ... faut bien pouvoir se logger
//                        .requestMatchers("/api/secure/test/login/**").permitAll()
//                        // Autorisation des endpoints Websocket : la securite est geree manuellement dans le serveur websocket
//                        .requestMatchers("/samplewebsocketserver/**").permitAll()
//                        // Toutes les autres requetes necessitent une authentification
//
//                        .requestMatchers("/users/**", "/login/**", "/web/test").permitAll()//hasRole("ADMIN")
////                        .requestMatchers("/add/**", "/users/**", "/admin/**", "/web/test").hasRole("ADMIN")
//
//                        .requestMatchers("/api/login/**","/api/chatrooms/create/**","/api/chatrooms/create","/api/chatrooms/myCreated","/api/chatrooms/myJoined").permitAll()
//                        .requestMatchers("/admin/**").permitAll()
//                        .requestMatchers("/users/add").hasRole("ADMIN")  // Ensure only admins can access the add user path
//                        .requestMatchers("/users/delete/**").permitAll()
//
//                        .anyRequest().authenticated()
//
//                )
//                // Desactivation Spring CSRF protection pour autoriser les requetes POST
//                .csrf(AbstractHttpConfigurer::disable)
//                // Application du filtre JWT
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//}

@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {
    private final JwtTokenFilter jwtTokenFilter;

    public CustomSecurityConfiguration(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/html/css/**", "/html/js/**").permitAll()
                        .requestMatchers("/api/open/test/**").permitAll()
                        .requestMatchers("/api/secure/test/login/**").permitAll()
                        .requestMatchers("/samplewebsocketserver/**").permitAll()
                        .requestMatchers("/api/login/**","/login","/users/login").permitAll()
                        .requestMatchers("/api/chatrooms/*","/api/chatrooms/create/**", "/api/chatrooms/create", "/api/chatrooms/myCreated", "/api/chatrooms/myJoined","/api/chatrooms/all","/api/chatrooms/addUser/*","/api/chatrooms/removeUser/*").permitAll()
                        .requestMatchers("/admin/**","/admin/accueil","/admin/users").hasRole("ADMIN")
                        .requestMatchers("/users/add","/users/addd").hasRole("ADMIN")
                        .requestMatchers("/users/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/chatrooms/delete/**").permitAll()
                        .requestMatchers("/api/users/all-for-login").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 允许所有来源，生产环境中应使用更严格的配置
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
