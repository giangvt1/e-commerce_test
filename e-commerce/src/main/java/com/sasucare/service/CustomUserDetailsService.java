package com.sasucare.service;

import com.sasucare.model.Role;
import com.sasucare.model.User;
import com.sasucare.repository.RoleUserRepository;
import com.sasucare.repository.UserRepository;
import com.sasucare.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final UserRepository userRepository;
    private final RoleUserRepository roleUserRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, RoleUserRepository roleUserRepository) {
        this.userRepository = userRepository;
        this.roleUserRepository = roleUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        logger.info("Found user: {} with ID: {}", user.getEmail(), user.getId());
        
        // Load roles directly from the database instead of relying on entity relationships
        List<Role> roles = roleUserRepository.findRolesByUserId(user.getId());
        logger.info("Roles loaded directly from database for user ID {}: {}", user.getId(), roles);
        
        return buildUserDetails(user, roles);
    }
    
    /**
     * Build UserDetails object from User entity
     * @param user User entity
     * @param roles User roles loaded directly from database
     * @return UserDetails object
     */
    private UserDetails buildUserDetails(User user, List<Role> roles) {
        // Get authorities from user roles
        Collection<SimpleGrantedAuthority> authorities = getAuthorities(user, roles);
        
        logger.info("Built authorities for user {}: {}", user.getEmail(), authorities);
        
        // Return our custom UserDetails implementation with additional user information
        return new CustomUserDetails(user, authorities);
    }
    
    /**
     * Get authorities from user roles
     * Maps both roles (ROLE_XXX) and permissions to authorities
     * @param user User entity
     * @param roles User roles loaded directly from database
     * @return Collection of authorities
     */
    private Collection<SimpleGrantedAuthority> getAuthorities(User user, List<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Add role-based authorities (ROLE_XXX)
        if (roles != null && !roles.isEmpty()) {
            logger.debug("Processing roles for user {}: {}", user.getEmail(), roles);
            
            for (Role role : roles) {
                // Log each role as we process it
                logger.debug("Processing role: ID={}, name={}", role.getId(), role.getName());
                
                // Add the role with ROLE_ prefix, ensuring we don't double-prefix
                String roleName = role.getName().toUpperCase();
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
                logger.debug("Adding authority: {}", authority.getAuthority());
                authorities.add(authority);
                
                // Process features/permissions
                if (role.getFeatures() != null) {
                    role.getFeatures().forEach(feature -> {
                        SimpleGrantedAuthority featureAuthority = new SimpleGrantedAuthority(feature.getName());
                        logger.debug("Adding feature authority: {}", featureAuthority.getAuthority());
                        authorities.add(featureAuthority);
                    });
                }
            }
        } else {
            logger.warn("No roles found for user: {}", user.getEmail());
            // For temporary development - if no roles, give user a basic customer role
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        
        logger.info("Final authorities for user {}: {}", user.getEmail(), authorities);
        return authorities;
    }
}
