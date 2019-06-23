package net.aulang.lang.oauth.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SHAUtils {
    public static String SHA256(final String src) {
        return SHA(src, "SHA-256");
    }

    public static String SHA512(final String src) {
        return SHA(src, "SHA-512");
    }

    private static String SHA(final String src, final String type) {
        String result = src;
        if (src != null && src.length() > 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(type);
                messageDigest.update(src.getBytes());
                byte[] bytes = messageDigest.digest();

                StringBuilder builder = new StringBuilder();
                for (byte b : bytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        builder.append('0');
                    }
                    builder.append(hex);
                }
                result = builder.toString();
            } catch (NoSuchAlgorithmException e) {
                /**
                 * 不会有此异常
                 */
                e.printStackTrace();
            }
        }
        return result;
    }
}
