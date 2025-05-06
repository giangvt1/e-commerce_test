package com.sasucare.security;

import com.sasucare.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Custom implementation of UserDetails that includes additional user information
 * like first name, last name, etc.
 */
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    
    // Additional user details
    private final String firstName;
    private final String lastName;
    private final Long userId;
    
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isActive();
        this.authorities = authorities;
        
        // Store additional user details
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userId = user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    // Getters for additional user details
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Get the full name of the user (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
