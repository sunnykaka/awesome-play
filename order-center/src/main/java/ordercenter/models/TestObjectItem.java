package ordercenter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.models.utils.EntityClass;
import common.utils.Money;
import ordercenter.constants.TestObjectItemStatus;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "test_object_item")
@Entity
public class TestObjectItem implements EntityClass<Integer> {

    private Integer id;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 产品SKU
     */
    private String productSku;


    //订单ID
    private Integer testObjectId;

    @JsonIgnore
    private TestObject testObject;

    /**
     * 状态
     */
    private TestObjectItemStatus status;


    /**
     * 一口价
     */
    private Money price = Money.valueOf(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_object_id", insertable = false, updatable = false)
    public TestObject getTestObject() {
        return testObject;
    }

    public void setTestObject(TestObject testObject) {
        this.testObject = testObject;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }


    @Column(name = "product_id")
    @Basic
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    @Column(name = "product_sku")
    @Basic
    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    @Column(name = "test_object_id")
    @Basic
    public Integer getTestObjectId() {
        return testObjectId;
    }

    public void setTestObjectId(Integer testObjectId) {
        this.testObjectId = testObjectId;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public TestObjectItemStatus getStatus() {
        return status;
    }

    public void setStatus(TestObjectItemStatus status) {
        this.status = status;
    }

}
