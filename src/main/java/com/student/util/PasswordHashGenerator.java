package com.student.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        String plainPassword = "password123";
        
        // Generate hash
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("\nCopy the hashed password to your INSERT statement");
        
        // Test verification
        boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("\nVerification test: " + matches);
    }
}
