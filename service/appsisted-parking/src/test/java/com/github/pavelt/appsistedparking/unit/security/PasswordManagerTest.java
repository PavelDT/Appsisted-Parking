package com.github.pavelt.appsistedparking.unit.security;

import com.github.pavelt.appsistedparking.security.PasswordManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
public class PasswordManagerTest {

    @Test
    public void hashPassword() {
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
