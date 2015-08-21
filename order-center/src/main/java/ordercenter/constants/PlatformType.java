package ordercenter.constants;


import common.models.utils.ViewEnum;

/**
 * User: Baron.Zhang
 * Date: 14-1-7
 * Time: 下午5:14
 */
public enum PlatformType implements ViewEnum {

    WEB("网站");


    public String value;

    PlatformType(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String getValue() {
        return value;
    }


}
