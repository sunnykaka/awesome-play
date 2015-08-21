package common.utils;

import play.data.validation.ValidationError;

import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-29.
 */
public class FormUtils {
    public static String showErrorInfo(Map<String, List<ValidationError>> errors) {

        StringBuilder error = new StringBuilder();
        errors.forEach((k, v) -> {
            if(v != null && !v.isEmpty()) {
                error.append(v.get(0).message());
            }
        });
        return error.toString();
    }
}
