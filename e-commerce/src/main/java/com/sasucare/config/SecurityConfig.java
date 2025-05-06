package com.sasucare.config;

import com.sasucare.security.CustomAuthenticationSuccessHandler;
// JWT imports removed
import com.sasucare.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// Removed unused imports

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authSuccessHandler;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService, 
                         CustomAuthenticationSuccessHandler authSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // apply to all paths
            .securityMatcher("/**")
            .authorizeHttpRequests(authorize -> authorize
                // public resources
                .requestMatchers("/", "/home", "/shop", "/about-us", "/contact", "/faqs").permitAll()
                .requestMatchers("/product/**", "/category/**", "/search").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/register", "/register-seller", "/login", "/forgot-password", "/verify").permitAll()
                
                // JWT API authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // customer and seller account resources
                .requestMatchers("/account/**").hasAnyRole("CUSTOMER", "SELLER")
                .requestMatchers("/checkout/**", "/cart/**").hasAnyRole("CUSTOMER", "SELLER")
                
                // seller-specific resources
                .requestMatchers("/seller/**").hasRole("SELLER")
                .requestMatchers("/api/seller/**").hasRole("SELLER")
                
                // admin resources
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // API access control - additional fine-grained control
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "SELLER")
                
                // Moderator-specific resources
                .requestMatchers("/moderator/**").hasRole("MODERATOR")
                .requestMatchers("/api/reviews/moderate/**").hasAnyRole("ADMIN", "MODERATOR")
                
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            // Enable form login for browser access
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authSuccessHandler)
                .failureUrl("/login?error=true") 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .tokenValiditySeconds(86400) // 1 day
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            // Only APIs use stateless sessions, web frontend uses standard sessions
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .csrf(csrf -> csrf.disable());  // dev only

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
