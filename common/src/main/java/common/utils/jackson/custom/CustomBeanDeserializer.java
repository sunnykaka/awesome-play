package common.utils.jackson.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by liubin on 15-4-21.
 */
public class CustomBeanDeserializer extends BeanDeserializer {

    public CustomBeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }

    public CustomBeanDeserializer(BeanDeserializerBase src) {
        super(src);
    }

    public CustomBeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
    }

    public CustomBeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
        super(src, unwrapper);
    }

    public CustomBeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
        super(src, oir);
    }

    public CustomBeanDeserializer(BeanDeserializerBase src, HashSet<String> ignorableProps) {
        super(src, ignorableProps);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {

        if(ctxt instanceof CustomDeserializationContext) {
            ((CustomDeserializationContext)ctxt).beanType = _beanType;
        }

        return super.deserialize(jp, ctxt, bean);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        if(ctxt instanceof CustomDeserializationContext) {
            ((CustomDeserializationContext)ctxt).beanType = _beanType;
        }

        return super.deserialize(jp, ctxt);
    }
}
