package common.models.utils;

import org.joda.time.DateTime;

/**
 * User: liubin
 * Date: 14-3-12
 */
public interface OperableData {

    void setCreateTime(DateTime createTime);

    DateTime getCreateTime();

    default void setUpdateTime(DateTime updateTime) {}

    default DateTime getUpdateTime() {return null;}

    default void setOperatorId(Integer operatorId){}

    default Integer getOperatorId() {return null;}

}
