package com.github.pavelt.appsistedparking.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.UUID;

public class PasswordManager {

    // singleton instance
    private static PasswordManager instance;

    private final int MEM_USAGE = 64 * 1024;
    private final int ITERATONS = 10;
    // 1 thread to be intentionally slow
    private final int PARALLELIZM = 1;
    // argon2 instance
    private Argon2 argon2;

    // singleton
    private PasswordManager() {
        argon2 = Argon2Factory.create();
    }

    /**
     * Singleton for the password manager
     * @return instance of PasswordManager
     */
    public static PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    /**
     * Hashes a password using the Argon2 library
     * @param salt - salt used during hashing
     * @param plainTextPassword - the password to be hashed
     * @return String representation of the hashed password
     */
    public String hashPassword(String salt, String plainTextPassword) {
        // array storing password as chars
        char[] password = (salt + plainTextPassword).toCharArray();
        // return the hash and clear the array storing the plain password
        try {
            return argon2.hash(MEM_USAGE, ITERATONS, PARALLELIZM, password);
        } finally {
            // empty the array used to store the password
            argon2.wipeArray(password);
        }
    }

    /**
     * Verifies that a password and its salt match a hash
     * @param hash - Argon2 hash of the password and salt
     * @param salt - The salt used during hashing
     * @param plainTextPassword - The password in plain text format
     * @return boolean representing if the password and salt matched the hash
     */
    public boolean verifyPassword(String hash, String salt, String plainTextPassword) {
        return argon2.verify(hash, salt + plainTextPassword);
    }

    /**
     * Generates a UUID based salt - good length, fast to generate.
     * @return UUID as a String
     */
    public String generateSalt() {
        return UUID.randomUUID().toString();
    }
}
