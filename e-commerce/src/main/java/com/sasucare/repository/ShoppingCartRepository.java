package com.sasucare.repository;

import org.springframework.stereotype.Repository;

/**
 * This is a stub repository that doesn't actually persist ShoppingCart objects.
 * ShoppingCart is a session-scoped entity not meant for database persistence.
 */
@Repository
public class ShoppingCartRepository {
    // This is intentionally empty to satisfy Spring's bean creation 
    // without actually trying to use JPA for the session-scoped ShoppingCart
}
