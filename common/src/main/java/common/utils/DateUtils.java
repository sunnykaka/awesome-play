package common.utils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * 日期时间处理工具
 * User: Asion
 * Date: 12-3-15
 * Time: 下午2:36
 */
public class DateUtils {
    /**
     * 简单的日期格式：yyyyMMdd，用于支付 add by lidujun
     */
    public static String SIMPLE_DATE_FORMAT_STR = "yyyyMMdd";

    /**
     * 简单的时间格式：yyyyMMddHHmmss，用于支付 add by lidujun
     */
    public static String SIMPLE_DATE_TIME_FORMAT_STR = "yyyyMMddHHmmss";

    /**
     * 通用的时间格式：yyyy-MM-dd HH:mm:ss，用于支付 add by lidujun
     */
    public static String DATE_TIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前系统时间(原始格式)
     */
    public static DateTime current() {
        return new DateTime();
    }

    public static DateTime parseDateTime(String str) {
        return parse(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static DateTime parseDate(String str) {
        return parse(str, "yyyy-MM-dd");
    }

    public static DateTime parse(String str, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.parseDateTime(str);
    }

    public static String printDateTime(DateTime dateTime) {
        return print(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 通用的日期格式类 add by lidujun
     * @param dateTime
     * @param pattern 时间格式
     * @return
     */
    public static String printDateTime(DateTime dateTime, String pattern) {
        return print(dateTime, pattern);
    }

    public static String printDate(DateTime dateTime) {
        return print(dateTime, "yyyy-MM-dd");
    }

    /**
     * 判断两个日期是否相等,忽略毫秒,因为mysql不存储毫秒
     * @param dateTime1
     * @param dateTime2
     * @return
     */
    public static boolean equals(DateTime dateTime1, DateTime dateTime2) {
        if(dateTime1 == null || dateTime2 == null) return false;
        return dateTime1.withMillisOfSecond(0).equals(dateTime2.withMillisOfSecond(0));
    }

    public static String print(DateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.print(dateTime);
    }

    /**
     * 打印结束时间到当前时间的信息.例如: 2天13时22分11秒
     * @param endTime
     * @return
     */
    public static String printDeadlineFromNow(DateTime endTime) {
        DateTime now = current();
        Period period;
        if(endTime != null && now.isBefore(endTime)) {
            period = new Period(now, endTime);
        } else {
            period = new Period(0);
        }

        PeriodFormatter periodFormatter = new PeriodFormatterBuilder().printZeroAlways().appendDays().appendSuffix("天")
                .appendHours().appendSuffix("时").appendMinutes().appendSuffix("分")
                .appendSeconds().appendSuffix("秒").toFormatter();

        return periodFormatter.print(period);

    }

}
