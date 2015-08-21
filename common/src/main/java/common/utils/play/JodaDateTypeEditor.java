package common.utils.play;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * 日期转换器
 * <p/>
 * 根据日期字符串长度判断是长日期还是短日期。
 *
 * @author liubin
 */
public class JodaDateTypeEditor extends PropertyEditorSupport {


    public static final String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final int LONG_FORMAT_LENGTH = LONG_FORMAT.length();
    public final DateTimeFormatter DF_LONG = DateTimeFormat.forPattern(LONG_FORMAT);

    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final int MINUTE_FORMAT_LENGTH = MINUTE_FORMAT.length();
    public final DateTimeFormatter DF_MINUTE = DateTimeFormat.forPattern(MINUTE_FORMAT);

    public static final String HOUR_FORMAT = "yyyy-MM-dd HH";
    public static final int HOUR_FORMAT_LENGTH = HOUR_FORMAT.length();
    public final DateTimeFormatter DF_HOUR = DateTimeFormat.forPattern(HOUR_FORMAT);

    public static final String SHORT_FORMAT = "yyyy-MM-dd";
    public static final int SHORT_FORMAT_LENGTH = SHORT_FORMAT.length();
    public final DateTimeFormatter DF_SHORT = DateTimeFormat.forPattern(SHORT_FORMAT);


    public static final String ONLY_TIME_FORMAT = "HH:mm";
    public static final int ONLY_TIME_FORMAT_LENGTH = ONLY_TIME_FORMAT.length();
    public final DateTimeFormatter DF_ONLY_HOUR = DateTimeFormat.forPattern(ONLY_TIME_FORMAT);



    public void setAsText(String text) throws IllegalArgumentException {
        text = text.trim();
        if (StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }

        if (text.length() <= ONLY_TIME_FORMAT_LENGTH) {
            setValue(DF_ONLY_HOUR.parseDateTime(text));
        } else if (text.length() <= SHORT_FORMAT_LENGTH) {
            setValue(DF_SHORT.parseDateTime(text));
        } else if (text.length() <= HOUR_FORMAT_LENGTH) {
            setValue(DF_HOUR.parseDateTime(text));
        } else if (text.length() <= MINUTE_FORMAT_LENGTH) {
            setValue(DF_MINUTE.parseDateTime(text));
        } else {
            setValue(DF_LONG.parseDateTime(text));
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     */
    public String getAsText() {
        DateTime value = (DateTime) getValue();
        return (value != null ? DF_LONG.print(value) : null);
    }
}
