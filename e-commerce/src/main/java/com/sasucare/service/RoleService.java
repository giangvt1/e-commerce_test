package com.sasucare.service;

import com.sasucare.model.Role;
import com.sasucare.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Find a role by name
     * @param name Role name
     * @return Optional containing the role if found
     */
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Get a role by name, creating it if it does not exist
     * @param name Role name
     * @param description Role description
     * @return Role
     */
    public Role getOrCreateRole(String name, String description) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        
        if (roleOpt.isPresent()) {
            return roleOpt.get();
        }
        
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        
        return roleRepository.save(role);
    }

    /**
     * Get or create the admin role
     * @return Admin role
     */
    public Role getOrCreateAdminRole() {
        return getOrCreateRole("ROLE_ADMIN", "Administrator with full system access");
    }

    /**
     * Get or create the customer role
     * @return Customer role
     */
    public Role getOrCreateCustomerRole() {
        return getOrCreateRole("ROLE_CUSTOMER", "Regular customer");
    }
    
    /**
     * Get or create the seller role
     * @return Seller role
     */
    public Role getOrCreateSellerRole() {
        return getOrCreateRole("ROLE_SELLER", "Shop owner/seller");
    }

    /**
     * Get or create the moderator role
     * @return Moderator role
     */
    public Role getOrCreateModeratorRole() {
        return getOrCreateRole("ROLE_MODERATOR", "Moderator with content review permissions");
    }

    /**
     * Get all roles
     * @return List of all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
