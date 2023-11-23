package cs308.group7.usms.utils;

/**
 * A utility class for encrypting and decrypting passwords
 */
public class Password {

    private static final int CAESAR_SHIFT = 13;

    /**
     * Checks if a given password matches an encrypted password
     * @param password The unencrypted password to check
     * @param encryptedPassword The encrypted password to check against
     * @return Whether the passwords match
     */
    public static boolean matches(String password, String encryptedPassword) {
        return password.equals(decrypt(encryptedPassword));
    }

    public static String encrypt(String unencryptedPass) { return caesar(unencryptedPass, CAESAR_SHIFT); }

    public static String decrypt(String encryptedPass) { return caesar(encryptedPass, 26 - CAESAR_SHIFT); }

    private static String caesar(String str, int shift) {
        StringBuilder res = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                res.append((char) ((c - base + shift) % 26 + base));
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

}
