package common.utils.play;

import common.utils.DateUtils;
import org.joda.time.DateTime;
import play.data.format.Formatters;

import java.text.ParseException;
import java.util.Locale;

/**
 * Created by liubin on 15-4-10.
 */
public class JodaDateFormatter extends Formatters.SimpleFormatter<DateTime> {


    @Override
    public DateTime parse(String text, Locale locale) throws ParseException {
        if(text == null || text.trim().isEmpty()) {
            return null;
        }
        JodaDateTypeEditor jodaDateTypeEditor = new JodaDateTypeEditor();
        jodaDateTypeEditor.setAsText(text);
        return (DateTime)jodaDateTypeEditor.getValue();

    }

    @Override
    public String print(DateTime value, Locale locale) {
        if(value == null) {
            return "";
        }
        return DateUtils.printDateTime(value);
    }
}
