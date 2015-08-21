package common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import common.utils.Money;

import java.io.IOException;

/**
 *
 */
public class MoneyDeserializer extends JsonDeserializer<Money> {

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        double money = jp.getDoubleValue();
        return Money.valueOf(money);
    }
}