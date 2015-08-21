package common.models.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import common.utils.jackson.EnumDeserializer;
import common.utils.jackson.EnumSerializer;

/**
 * User: liubin
 * Date: 14-5-21
 */
@JsonSerialize(using = EnumSerializer.class)
@JsonDeserialize(using = EnumDeserializer.class)
public interface ViewEnum {

    String getName();

    String getValue();

}
