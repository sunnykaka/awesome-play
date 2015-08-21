package common.utils;

import common.exceptions.AppException;
import common.utils.AES;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Play;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by liubin on 15-4-29.
 */
public class EncryptUtil {

    public static String encrypt(String content) throws AppException {
        if(StringUtils.isBlank(content)) {
            throw new AppException("待加密内容不能为空");
        }

        String secret = Play.application().configuration().getString("shop.secret");
        byte[] password = Base64.getDecoder().decode(secret);
        try {
            byte[] crypto = AES.encrypt(content.getBytes("UTF-8"), password);
            return Base64.getEncoder().encodeToString(crypto);

        } catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }

    }

    public static String decrypt(String cryptoInBase64) throws AppException {
        if(StringUtils.isBlank(cryptoInBase64)) {
            throw new AppException("待解密内容不能为空");
        }

        String secret = Play.application().configuration().getString("shop.secret");
        byte[] password = Base64.getDecoder().decode(secret);
        try {
            byte[] crypto = Base64.getDecoder().decode(cryptoInBase64);
            return new String(AES.decrypt(crypto, password), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }

    }


}
