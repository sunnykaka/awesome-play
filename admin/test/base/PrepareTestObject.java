package base;

import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.*;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 15/4/6.
 */
public abstract class PrepareTestObject extends BaseTest {

    protected void prepareTestObjects(int size, int itemSize) {
        doInTransaction(em -> {

            em.createQuery("delete from TestObjectItem").executeUpdate();
            em.createQuery("delete from TestObject").executeUpdate();

            //创建订单
            for (int i = 0; i < size; i++) {
                TestObject testObject = initTestObject(itemSize);

                em.persist(testObject);
                for(TestObjectItem testObjectItem : testObject.getTestObjectItemList()) {
                    testObjectItem.setTestObjectId(testObject.getId());
                    em.persist(testObjectItem);
                }
            }

            return null;
        });
    }

    protected TestObject initTestObject(int itemSize) {
        TestObject testObject = new TestObject();
        testObject.setBuyerId("买家" + RandomStringUtils.randomNumeric(8));
        testObject.setPlatformType(PlatformType.WEB);
        testObject.setStatus(TestObjectStatus.WAIT_PROCESS);
        testObject.setCreateTime(DateUtils.current());
        testObject.setUpdateTime(DateUtils.current());
        testObject.setBuyTime(DateUtils.current());
        testObject.setActualFee(Money.valueOf(3.23));
        testObject.setOrderNo(RandomStringUtils.randomAlphanumeric(8));


        List<TestObjectItem> testObjectItemList = new ArrayList<>();
        for (int j = 0; j < itemSize; j++) {
            TestObjectItem testObjectItem = new TestObjectItem();
            testObjectItem.setTestObjectId(testObject.getId());
            testObjectItem.setPrice(Money.valueOf(20));
            testObjectItem.setStatus(TestObjectItemStatus.NOT_SIGNED);
            testObjectItem.setProductId(Integer.parseInt(RandomStringUtils.randomNumeric(8)));
            testObjectItem.setProductSku(RandomStringUtils.randomAlphabetic(8));

            testObjectItemList.add(testObjectItem);
        }
        testObject.setTestObjectItemList(testObjectItemList);

        return testObject;
    }
}
