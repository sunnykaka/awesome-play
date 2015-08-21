package common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import common.models.utils.ViewEnum;
import common.utils.jackson.custom.CustomDeserializationContext;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
public class EnumDeserializer extends JsonDeserializer<ViewEnum> {

    @Override
    public ViewEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        if(!(ctxt instanceof CustomDeserializationContext)) {
            Logger.error("DeserializationContext的实现类CustomDeserializationContext, 无法反序列化枚举类型");
            return null;
        }
        /**
         * 因为使用了CustomerObjectMapper,所以可以得到属性的类型
         */
        JavaType beanType = ((CustomDeserializationContext) ctxt).beanType;

        JsonNode jsonNode = jp.getCodec().readTree(jp);
        JsonNode nameNode = jsonNode.get("name");
        if(nameNode == null || StringUtils.isBlank(nameNode.asText())) {
            Logger.warn("枚举类型在反序列化的时候发现json字符串里没有name属性, json: " + jsonNode.toString());
            return null;
        }

        String name = nameNode.asText();
        String fieldName = ctxt.getParser().getParsingContext().getCurrentName();

        try {
            Field field = beanType.getRawClass().getDeclaredField(fieldName);
            Class<?> enumType = field.getType();
            Method method = enumType.getDeclaredMethod("valueOf", String.class);
            return (ViewEnum)method.invoke(null, name);

        } catch (NoSuchFieldException e) {
            Logger.error(String.format("反序列化枚举的时候,%s类里没有找到%s属性", beanType.getRawClass().getName(), fieldName));
            return null;
        } catch (Exception e) {
            Logger.error("反序列化枚举失败", e);
            return null;
        }

    }

}