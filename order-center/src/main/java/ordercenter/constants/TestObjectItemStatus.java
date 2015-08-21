package ordercenter.constants;


import common.models.utils.ViewEnum;

/**
 * 订单明细状态
 * User: liubin
 * Date: 13-12-30
 */
public enum TestObjectItemStatus implements ViewEnum {

    NOT_SIGNED("未签收"),

    SIGNED("已签收");


    public String value;

    TestObjectItemStatus(String value) {
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
