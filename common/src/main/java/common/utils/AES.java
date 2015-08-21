package common.utils;

import common.exceptions.AppException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    public static byte[] encrypt(byte[] content, byte[] password)
            throws AppException {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static byte[] decrypt(byte[] content, byte[] password)
            throws AppException {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static byte[] initPasswordKey() throws AppException {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom());
            SecretKey secretKey = kgen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println(new String(Base64.getEncoder().encode(initPasswordKey()), "ISO8859-1"));
    }

}