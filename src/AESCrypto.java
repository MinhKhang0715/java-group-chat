import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESCrypto {
    private final static String key = "Bar12345Bar12345";
    private final static byte[] decodedKey = Base64.getDecoder().decode(key);
    private final static Key aesKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");

    public String encryption(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryption(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(input)));
        } catch (IllegalBlockSizeException |
                BadPaddingException |
                NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
