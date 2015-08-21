package common.utils.jackson.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;

/**
 * Created by liubin on 15-4-21.
 */
public class CustomDeserializationContext extends DefaultDeserializationContext {

    private static final long serialVersionUID = 1L;

    /**
     * 正在解析的属性的类型
     */
    public JavaType beanType;

    /**
     * Default constructor for a blueprint object, which will use the standard
     * {@link com.fasterxml.jackson.databind.deser.DeserializerCache}, given factory.
     */
    public CustomDeserializationContext(DeserializerFactory df) {
        super(df, null);
    }

    protected CustomDeserializationContext(CustomDeserializationContext src,
                                           DeserializationConfig config, JsonParser jp, InjectableValues values) {
        super(src, config, jp, values);
    }

    protected CustomDeserializationContext(CustomDeserializationContext src) { super(src); }

    protected CustomDeserializationContext(CustomDeserializationContext src, DeserializerFactory factory) {
        super(src, factory);
    }

    @Override
    public DefaultDeserializationContext copy() {
        if (getClass() != CustomDeserializationContext.class) {
            return super.copy();
        }
        return new CustomDeserializationContext(this);
    }


    @Override
    public DefaultDeserializationContext createInstance(DeserializationConfig config,
                                                        JsonParser jp, InjectableValues values) {
        return new CustomDeserializationContext(this, config, jp, values);
    }

    @Override
    public DefaultDeserializationContext with(DeserializerFactory factory) {
        return new CustomDeserializationContext(this, factory);
    }


}
