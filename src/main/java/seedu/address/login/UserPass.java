package seedu.address.login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//@@author ngshikang
/**
 * Represents a profile's username and password
 */
public class UserPass {

    private String username;
    private String password;

    public UserPass(String username, String password) {
        this.username = username;
        this.password = hash(password.trim());
    }

    /**
     * Returns a String containing the SHA-256 encrypted form of input password String
     */
    public static String hash(String password) {
        byte[] encodedPassword = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            encodedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return bytesToHex(encodedPassword);

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Utility function returning hexadecimal string from hashed password in byte array
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
