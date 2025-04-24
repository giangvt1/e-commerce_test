package com.ecommerce.util;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // Generate a secure random key of 64 bytes (512 bits)
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[64];
        secureRandom.nextBytes(key);
        
        // Encode it to Base64 for use in application.properties
        String secretKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated JWT Secret Key:");
        System.out.println(secretKey);
    }
}
