package common.utils.jackson.custom;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

/**
 *
 * 该ObjectMapper的作用是可以初始化CustomDeserializationContext,用来在反序列对象的时候,
 * 将对象属性的类型Class设置到CustomDeserializationContext里.
 *
 * Created by liubin on 15-4-21.
 */
public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        super(null, null, new CustomDeserializationContext(BeanDeserializerFactory.instance));
    }

    public CustomObjectMapper(JsonFactory jf) {
        super(jf, null, new CustomDeserializationContext(BeanDeserializerFactory.instance));
    }

    public CustomObjectMapper(ObjectMapper src) {
        super(src);
    }

    public CustomObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
        super(jf, sp, dc);
    }
}
