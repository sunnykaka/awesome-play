package common.utils;

import com.google.common.collect.Lists;
import play.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-22.
 */
public class UrlUtils {

    /**
     * 创建查询参数字符串
     * @param params
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static String buildQueryString(Map<String, List<String>> params) {
        if(params == null || params.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, List<String>> entry : params.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if(values == null || values.isEmpty()) {
                sb.append(key).append("=&");
            } else {
                for (String value : values) {
                    sb.append(key).append("=");
                    if(!org.apache.commons.lang3.StringUtils.isBlank(value)) {
                        try {
                            sb.append(URLEncoder.encode(value, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Logger.error("", e);
                        }
                    }
                    sb.append("&");
                }
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static Map<String, List<String>> disposeQueryString(String queryString) {
        Map<String, List<String>> params = new HashMap<>();
        if(org.apache.commons.lang3.StringUtils.isBlank(queryString)) {
            return params;
        }
        String[] array = queryString.split("&");
        for(String keyValue : array) {
            String[] keyValueArray = keyValue.split("=");
            try {
                String key = keyValueArray[0];
                String value = (keyValueArray.length < 2 || keyValueArray[1] == null) ? null : URLDecoder.decode(keyValueArray[1], "UTF-8");

                params.compute(key, (k, v) -> {
                    List<String> result = v == null ? new ArrayList<>() : v;
                    if(value != null) {
                        result.add(value);
                    }
                    return result;
                });

            } catch (UnsupportedEncodingException e) {
                Logger.error("", e);
            }

        }

        return params;
    }


    public static void main(String[] args) {

        Map<String, List<String>> params = new HashMap<>();
        params.put("param1", new ArrayList<>());
        params.put("param2", Lists.newArrayList("1"));
        params.put("param3", Lists.newArrayList("1", "2"));
        params.put("param4", Lists.newArrayList("1 2", "3 4"));

        Map<String, List<String>> newParams = disposeQueryString(buildQueryString(params));

        System.out.println();

    }


}
