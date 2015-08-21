package common.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import common.utils.Money;

import java.io.IOException;

/**
 *
 */
public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        if (value != null) {
            jsonGenerator.writeNumber(value.getAmount());
        }
    }
}  