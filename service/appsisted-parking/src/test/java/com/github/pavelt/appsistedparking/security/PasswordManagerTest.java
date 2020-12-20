package com.github.pavelt.appsistedparking.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class PasswordManagerTest {

    @Test
    void hashPassword() {
        // test checks if a password can be hashed correctly via the password manger
        // generateds a salt,
        // hashes the password
        // verifies that salt = password can be matched to hash
        String password = "password";
        String salt = PasswordManager.getInstance().generateSalt();
        String hash = PasswordManager.getInstance().hashPassword(salt, password);

        assertTrue("Check password matches", PasswordManager.getInstance().verifyPassword(hash, salt, password));
    }
}
