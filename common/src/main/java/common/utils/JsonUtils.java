package common.utils;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import common.utils.jackson.CustomDateTimeDeserializer;
import common.utils.jackson.CustomDateTimeSerializer;
import common.utils.jackson.custom.CustomBeanDeserializer;
import common.utils.jackson.custom.CustomObjectMapper;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * User: amos.zhou
 * Date: 13-12-23
 * Time: 下午12:10
 * Json工具类
 */
public class JsonUtils {

    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * 初始化ObjectMapper
     * @return
     */
    private static ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new CustomObjectMapper();

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //jodatime支持
        JodaModule jodaModule = new JodaModule();
        jodaModule.addDeserializer(DateTime.class, CustomDateTimeDeserializer.forType(DateTime.class));
        jodaModule.addSerializer(DateTime.class, new CustomDateTimeSerializer());
        objectMapper.registerModule(jodaModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

        //hibernate支持
        objectMapper.registerModule(new Hibernate4Module().enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING));

        //自定义ViewEnum反序列化支持
        objectMapper.registerModule(new SimpleModule() {

            public void setupModule(SetupContext context) {
                super.setupModule(context);

                context.addBeanDeserializerModifier(new BeanDeserializerModifier() {
                    @Override
                    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                                  BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                        if(deserializer instanceof BeanDeserializerBase) {
                            return new CustomBeanDeserializer((BeanDeserializerBase)deserializer);
                        }

                        return deserializer;
                    }

                });
            }
        });

        return objectMapper;
    }

    public static String object2Json(Object o) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = new JsonFactory().createGenerator(sw);
            OBJECT_MAPPER.writeValue(gen, o);
        } catch (IOException e) {
            throw new RuntimeException("不能序列化对象为Json", e);
        } finally {
            if (null != gen) {
                try {
                    gen.close();
                } catch (IOException e) {
                    throw new RuntimeException("不能序列化对象为Json", e);
                }
            }
        }
        return sw.toString();
    }


    /**
     * 将 json 字段串转换为 对象.
     *
     * @param json  字符串
     * @param clazz 需要转换为的类
     * @return
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("将 Json 转换为对象时异常,数据是:" + json, e);
        }
    }

    /**
     *   将 json 字段串转换为 List.
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> json2List(String json,Class<T> clazz) throws IOException {
        JavaType type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);

        List<T> list = OBJECT_MAPPER.readValue(json, type);
        return list;
    }


    /**
     *  将 json 字段串转换为 数据.
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T>  T[] json2Array(String json,Class<T[]> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);

    }

    public static <T> T node2Object(JsonNode jsonNode, Class<T> clazz) {
        try {
            T t = OBJECT_MAPPER.treeToValue(jsonNode, clazz);
            return t;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("将 Json 转换为对象时异常,数据是:" + jsonNode.toString(), e);
        }
    }

    public static JsonNode object2Node(Object o) {
        try {
            if(o == null) {
                return OBJECT_MAPPER.createObjectNode();
            } else {
                return OBJECT_MAPPER.convertValue(o, JsonNode.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("不能序列化对象为Json", e);
        }
    }

}
