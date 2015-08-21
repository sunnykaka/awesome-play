package common.utils;

/**
 * Created by zhb on 15-5-15.
 */
public class StringUtils {

    /**
     * 手机号码加密
     *
     * @param tel
     * @return
     */
    public static String getSecurityMobile(String tel) {
        return tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 用户名加密
     *
     * @param name
     * @return
     */
    public static String getSecurityName(String name) {

        if (null == name || "".equals(name)) {
            return name;
        }

        if (name.length() < 4) {
            return name.substring(0, 1) + "***" + name.substring(name.length() - 1, name.length());
        }

        if (name.length() >= 4) {
            return name.substring(0, 3) + "***" + name.substring(name.length() - 1, name.length());
        }

        return name;
    }

}
