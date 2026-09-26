package com.example.opensearchassistant.config;

import com.example.opensearchassistant.model.AdminUser;
import com.example.opensearchassistant.repository.AdminUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(AdminUserRepository adminUserRepository) {
        return username -> adminUserRepository.findByUsername(username)
                .map(adminUser -> User.withUsername(adminUser.getUsername())
                        .password(adminUser.getPassword())
                        .roles(adminUser.getRole().replace("ROLE_", "").replace("ADMIN", "ADMIN"))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/api/admin/login", "/h2-console/**", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
