package common.services;

import base.BaseTest;
import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.PlatformType;
import ordercenter.constants.TestObjectItemStatus;
import ordercenter.constants.TestObjectStatus;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-4-2.
 */
public class GeneralDaoTest extends BaseTest{


    void prepareTestObjects(int size, int itemSize) {
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

    TestObject initTestObject(int itemSize) {
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

    /**
     * 测试merge和update
     */
    @Test
    public void testGeneralDaoMergeAndUpdate() {

        prepareTestObjects(0, 0);

        TestObject testObject1 = doInTransactionWithGeneralDao(generalDao -> {

            TestObject testObject = new TestObject();
            testObject.setOrderNo(RandomStringUtils.randomAlphanumeric(8));
            testObject.setPlatformType(PlatformType.WEB);
            testObject.setStatus(TestObjectStatus.WAIT_PROCESS);

            generalDao.persist(testObject);
            generalDao.flush();
            generalDao.detach(testObject);

            assert testObject.getCreateTime() != null;
            assert testObject.getUpdateTime() != null;
            assert testObject.getId() > 0;

            //此时更新无用,因为对象已没有被session管理(显式detach)
            testObject.setStatus(TestObjectStatus.INVALID);

            return testObject;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            //校验之前的更新确实没起作用
            TestObject testObject = generalDao.get(TestObject.class, testObject1.getId());
            assert testObject.getStatus() == TestObjectStatus.WAIT_PROCESS;
            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            //merge,会根据testObject1的id从数据库load出testObject2对象,再把testObject1的属性拷给testObject2
            testObject1.setStatus(TestObjectStatus.INVALID);
            TestObject testObject2 = generalDao.merge(testObject1);
            assert testObject2.getStatus() == testObject1.getStatus();

            //对order1的更新无用,对testObject2的更新有用.因为testObject1没有被session管理
            testObject1.setStatus(TestObjectStatus.PRINTED);
            testObject2.setStatus(TestObjectStatus.INVOICED);

            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {
            //校验确实是testObject2的更新起作用
            TestObject testObject = generalDao.get(TestObject.class, testObject1.getId());
            assert testObject.getStatus() == TestObjectStatus.INVOICED;
            return null;
        });

        //测试persist能够修改持久态对象
        doInTransactionWithGeneralDao(generalDao -> {

            TestObject testObject = generalDao.get(TestObject.class, testObject1.getId());
            testObject.setStatus(TestObjectStatus.INVALID);
            generalDao.persist(testObject);
            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {
            //校验确实是testObject2的更新起作用
            TestObject testObject = generalDao.get(TestObject.class, testObject1.getId());
            assert testObject.getStatus() == TestObjectStatus.INVALID;
            return null;
        });


        //测试update方法
        doInTransactionWithGeneralDao(generalDao -> {

            Map<String, Object> params = new HashMap<>();
            params.put("status", TestObjectStatus.SIGNED);
            params.put("id", testObject1.getId());


            int update = generalDao.update(" update TestObject o set o.status = :status where o.id = :id ", params);
            assert update == 1;

            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            TestObject order = generalDao.get(TestObject.class, testObject1.getId());
            assert order.getStatus() == TestObjectStatus.SIGNED;
            return null;
        });

    }

    /**
     * 测试findAll
     */
    @Test
    public void testGeneralDaoFindAll() {

        prepareTestObjects(50, 3);

        doInTransactionWithGeneralDao(generalDao -> {

            List<TestObject> testObjects = generalDao.findAll(TestObject.class);
            List<TestObjectItem> testObjectItems = generalDao.findAll(TestObjectItem.class);

            assert testObjects.size() == 50;
            assert testObjectItems.size() == 50 * 3;

            return null;

        });

    }

}
