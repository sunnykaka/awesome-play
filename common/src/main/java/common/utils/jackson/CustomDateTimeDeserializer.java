package common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import common.utils.play.JodaDateTypeEditor;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import java.io.IOException;

/**
 * Created by liubin on 15-3-27.
 */
public class CustomDateTimeDeserializer extends DateTimeDeserializer {

    public CustomDateTimeDeserializer(Class<? extends ReadableInstant> cls) {
        super(cls);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls) {
        return (JsonDeserializer<T>) new CustomDateTimeDeserializer(cls);
    }


    @Override
    public ReadableDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText();
            if(str == null || "".equals(str.trim())) return null;
            str = str.trim();
            JodaDateTypeEditor e = new JodaDateTypeEditor();
            e.setAsText(str);
            return (ReadableDateTime)e.getValue();
        } else {
            return super.deserialize(jp, ctxt);
        }
    }
}
