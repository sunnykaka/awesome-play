package common.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import common.utils.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Created by liubin on 15-3-27.
 */
public class CustomDateTimeSerializer extends JodaDateSerializerBase<DateTime> {

    protected final static JacksonJodaDateFormat DEFAULT_FORMAT
            = new JacksonJodaDateFormat(ISODateTimeFormat.dateTime().withZoneUTC());


    public CustomDateTimeSerializer() {
        super(DateTime.class, DEFAULT_FORMAT, false, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public JodaDateSerializerBase withFormat(JacksonJodaDateFormat format) {
        return new CustomDateTimeSerializer();
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(DateUtils.printDateTime(value));
        }

    }

}
