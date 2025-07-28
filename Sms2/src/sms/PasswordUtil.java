package sms;

import java.security.MessageDigest;
import java.util.Random;

public class PasswordUtil {

    // Generate a random 6-digit password
    public static String generateRandomPassword() {
        Random random = new Random();
        int password = 100000 + random.nextInt(900000); // ensures a 6-digit number
        return String.valueOf(password);
    }

    // Hash a password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }
}
