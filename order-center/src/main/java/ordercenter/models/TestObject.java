package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.PlatformType;
import ordercenter.constants.TestObjectStatus;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "test_object")
@Entity
public class TestObject implements EntityClass<Integer>, OperableData {

    private Integer id;

    private String orderNo;

    /**
     * 状态
     */
    private TestObjectStatus status;


    /**
     * 金额
     */
    @Required
    private Money actualFee = Money.valueOf(0);

    /**
     * 买家ID
     */
    @MinLength(3)
    private String buyerId;


    /**
     * 下单时间
     */
    private DateTime buyTime;


    private DateTime createTime;

    private DateTime updateTime;

    //操作人ID
    private Integer operatorId;

    /**
     * 平台类型
     */
    private PlatformType platformType;

    private List<TestObjectItem> testObjectItemList = new ArrayList<>(0);


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testObject")
    public List<TestObjectItem> getTestObjectItemList() {
        return testObjectItemList;
    }

    public void setTestObjectItemList(List<TestObjectItem> testObjectItemList) {
        this.testObjectItemList = testObjectItemList;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public TestObjectStatus getStatus() {
        return status;
    }

    public void setStatus(TestObjectStatus status) {
        this.status = status;
    }


    @Column(name = "actual_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getActualFee() {
        return actualFee;
    }

    public void setActualFee(Money actualFee) {
        this.actualFee = actualFee;
    }

    @Column(name = "buyer_id")
    @Basic
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }


    @Column(name = "buy_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(DateTime buyTime) {
        this.buyTime = buyTime;
    }


    @Column(name = "create_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }


    @Column(name = "update_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "operator_id")
    @Basic
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Column(name = "platform_type")
    @Enumerated(EnumType.STRING)
    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    @Column(name = "order_no")
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
