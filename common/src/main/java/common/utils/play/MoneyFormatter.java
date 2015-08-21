package common.utils.play;

import common.utils.Money;
import play.data.format.Formatters;

import java.text.ParseException;
import java.util.Locale;

/**
 * Created by liubin on 15-4-10.
 */
public class MoneyFormatter extends Formatters.SimpleFormatter<Money> {


    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        if(text == null || text.trim().isEmpty()) {
            return null;
        }
        return Money.valueOf(text);
    }

    @Override
    public String print(Money value, Locale locale) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
}
