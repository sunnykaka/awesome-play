package ordercenter.constants;

import common.models.utils.ViewEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单状态
 * User: liubin
 * Date: 13-12-30
 */
public enum TestObjectStatus implements ViewEnum {

    /**
     * 手动创建订单,待审核 *
     */
    WAIT_APPROVE("待审核", "A"),

    /**
     * 订单已经作废处理
     */
    INVALID("已作废", "AA"),

    /**
     * 订单进入系统中，等待客服处理 *
     */
    WAIT_PROCESS("待处理", "AB"),


    /**
     * 客服已审单，流转至仓库 *
     */
    CONFIRMED("已确认", "ABA"),

    /**
     * 仓库已打印快递单、发货单，商品等待验货 *
     */
    PRINTED("已打印", "ABAA"),

    /**
     * 仓库已完成验货，等待封箱发货 *
     */
    EXAMINED("已验货", "ABAAA"),

    /**
     * 商品已有快递单号已发货出库 *
     */
    INVOICED("已发货", "ABAAAA"),

    /**
     * 顾客已签收商品 *
     */
    SIGNED("已签收", "ABAAAAA");


    public String value;
    private String code;

    /**
     * 根据代码得到状态对象
     *
     * @param code
     * @return
     */
    public static TestObjectStatus getByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        for (TestObjectStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    TestObjectStatus(String value, String code) {
        this.value = value;
        this.code = code;
    }


    public String getCode() {
        return this.code;
    }


    /**
     * 获取下一步允许的操作
     * 借鉴Trie树的思想
     *
     * @param strict 如果为true,只能得到字节点,否则能够得到所有孙子节点,返回的列表顺序是实例属性的声明顺序
     * @return
     */
    public List<TestObjectStatus> getNextValidStatusList(boolean strict) {
        List<TestObjectStatus> result = new ArrayList<TestObjectStatus>();
        TestObjectStatus[] statusArray = TestObjectStatus.values();
        for (TestObjectStatus os : statusArray) {
            if (strict) {
                //属于这条分支，而且是属于下一步操作
                if (this.isParentOf(os)) {
                    result.add(os);
                }
            } else {
                if (this.isAncestorOf(os)) {
                    result.add(os);
                }
            }
        }
        return result;
    }

    /**
     * 能否从当前状态向后跳转为os
     *
     * @param os
     * @param strict 如果为true,只能跳到字节点,否则能够跳到孙子节点
     * @return
     */
    public boolean canNextTo(TestObjectStatus os, boolean strict) {
        if (TestObjectStatus.WAIT_PROCESS == this) {
            //待处理只能变为已作废或已确认
            return os == TestObjectStatus.INVALID || os == TestObjectStatus.CONFIRMED;
        }
        return getNextValidStatusList(strict).contains(os);
    }


    /**
     * 是否可以往后回滚
     *
     * @param os
     * @param strict 如果为true,只能回到父节点,否则能够回到祖先节点
     * @return
     */
    public boolean canBackTo(TestObjectStatus os, boolean strict) {
        //已经经过了“已发货”的阶段，不能进行任何回滚操作，
        if (this.code.startsWith(INVOICED.code)) return false;
        if (strict) {
            return os.isParentOf(this);
        } else {
            return os.isAncestorOf(this);
        }
    }

    /**
     * 是否是给出节点的父节点
     *
     * @param other
     * @return
     */
    public boolean isParentOf(TestObjectStatus other) {
        return this.isAncestorOf(other) && this.code.length() + 1 == other.getCode().length();
    }

    /**
     * 是否是给出节点的祖先节点
     *
     * @param other
     * @return
     */
    public boolean isAncestorOf(TestObjectStatus other) {
        return other != this && other.getCode().startsWith(this.code);
    }

    public static void main(String[] args) {
//        Assert.assertFalse(OrderStatus.SIGNED.canBackTo(OrderStatus.CONFIRMED));
//        List<OrderStatus> list  = OrderStatus.WAIT_PROCESS.getNextValidStatusList();
//        System.out.println(list);
//        System.out.println(CONFIRMED.canNextTo(EXAMINED));
//        System.out.println(WAIT_PROCESS.canNextTo(SIGNED));
    }

    /**
     * 得到以当前节点为起点,to节点为终点的所有节点(包括起点和终点)
     *
     * @param to
     * @return
     */
    public List<TestObjectStatus> getAllNodesOnPath(TestObjectStatus to) {
        List<TestObjectStatus> orderStatusList = new ArrayList<TestObjectStatus>();
        if (!this.isAncestorOf(to)) return orderStatusList;
        String fromCode = this.getCode();
        String toCode = to.getCode();
        for (int i = fromCode.length(); i <= toCode.length(); i++) {
            orderStatusList.add(TestObjectStatus.getByCode(toCode.substring(0, i)));
        }
        return orderStatusList;
    }


    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

}
