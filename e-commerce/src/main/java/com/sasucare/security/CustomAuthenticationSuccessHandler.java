package com.sasucare.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Custom authentication success handler that redirects users based on their roles
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Get the user's authorities/roles
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        // Redirect based on role
        if (roles.contains("ROLE_SELLER")) {
            // Seller goes to seller dashboard
            response.sendRedirect("/seller/dashboard");
        } else if (roles.contains("ROLE_ADMIN")) {
            // Admin goes to admin dashboard
            response.sendRedirect("/admin/dashboard");
        } else {
            // Everyone else goes to home page
            response.sendRedirect("/home");
        }
    }
}
