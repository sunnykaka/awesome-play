package ordercenter.services;

import base.PrepareTestObject;
import common.utils.DateUtils;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.constants.PlatformType;
import ordercenter.constants.TestObjectItemStatus;
import ordercenter.constants.TestObjectStatus;
import ordercenter.dtos.TestObjectSearcher;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import utils.Global;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Created by liubin on 15-4-2.
 */
public class TestObjectServiceTest extends PrepareTestObject{

    @Test
    public void testCRUDInOrder() {
        prepareTestObjects(0, 0);

        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);
        String orderNo = RandomStringUtils.randomNumeric(8);
        //创建测试对象
        TestObject testObject1 = new TestObject();
        Integer testObjectId = doInTransaction(em -> {
            testObject1.setOrderNo(orderNo);
            testObject1.setPlatformType(PlatformType.WEB);
            testObject1.setStatus(TestObjectStatus.WAIT_PROCESS);
            testObject1.setCreateTime(DateUtils.current());
            testObject1.setUpdateTime(DateUtils.current());

            em.persist(testObject1);

            assert testObject1.getId() != null;
            assert testObject1.getId() >= 0;

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DateTime createTime2 = DateUtils.current();
            testObject1.setCreateTime(createTime2);
            em.flush();

            em.detach(testObject1);

            TestObject testObject2 = em.find(TestObject.class, testObject1.getId());
            assert DateUtils.equals(testObject2.getCreateTime(), createTime2);

            //创建测试项
            TestObjectItem testObjectItem = new TestObjectItem();
            testObjectItem.setTestObjectId(testObject1.getId());
            testObjectItem.setPrice(Money.valueOf(20));
            testObjectItem.setStatus(TestObjectItemStatus.NOT_SIGNED);
            testObjectItem.setProductId(Integer.parseInt(RandomStringUtils.randomNumeric(8)));
            testObjectItem.setProductSku(RandomStringUtils.randomAlphabetic(8));

            em.persist(testObjectItem);

            return testObject1.getId();
        });

        //测试findByKey方法
        List<TestObject> testObjects = testObjectService.findByKey(empty(), of(orderNo), empty(), empty(), empty());
        assert testObjects.size() == 1;
        TestObject testObject = testObjects.get(0);
        assert testObject.getId().equals(testObjectId);

        testObjects = testObjectService.findByKey(empty(), of(RandomStringUtils.randomAlphanumeric(10)), empty(), empty(), empty());
        assert testObjects.size() == 0;
        testObjects = testObjectService.findByKey(empty(), empty(), empty(), empty(), empty());
        assert testObjects.size() > 0;

        testObjects = testObjectService.findByKey(empty(), empty(), empty(), of(testObject.getCreateTime()), of(testObject.getCreateTime()));
        assert testObjects.size() == 1;
        testObject = testObjects.get(0);
        assert testObject.getId().equals(testObjectId);

        testObjects = testObjectService.findByKey(empty(), empty(), empty(), of(testObject.getCreateTime()), empty());
        assert testObjects.size() == 1;
        testObject = testObjects.get(0);
        assert testObject.getId().equals(testObjectId);

    }

    @Test
    public void testOrderPage() {

        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);

        prepareTestObjects(50, 3);

        //测试分页方法
        List<TestObject> testObjects = testObjectService.findByKey(of(new Page<>(1, Page.DEFAULT_PAGE_SIZE)), empty(), of(TestObjectStatus.WAIT_PROCESS), empty(), empty());
        assert testObjects.size() == Page.DEFAULT_PAGE_SIZE;
        testObjects = testObjectService.findByKey(of(new Page<>(2, 20)), empty(), empty(), empty(), empty());
        assert testObjects.size() == 20;
    }

    @Test
    public void testFindByComplicateKey() {
        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);

        prepareTestObjects(50, 3);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKey);
        prepareTestObjects(50, 1);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKey);
    }

    @Test
    public void testFindByComplicateKeyWithJpql() {
        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);

        prepareTestObjects(50, 3);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKeyWithJpql);
        prepareTestObjects(50, 1);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKeyWithJpql);
    }

    @Test
    public void testFindByComplicateKeyWithGeneralDaoQuery() {
        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);

        prepareTestObjects(50, 3);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKeyWithGeneralDaoQuery);
        prepareTestObjects(50, 1);
        runTestFindByComplicateMethodJpql(testObjectService::findByComplicateKeyWithGeneralDaoQuery);

    }

    private void runTestFindByComplicateMethodJpql(BiFunction<Optional<Page<TestObject>>, TestObjectSearcher, List<TestObject>> method) {

        TestObjectSearcher testObjectSearcher = new TestObjectSearcher();
        testObjectSearcher.status = TestObjectStatus.WAIT_PROCESS;
        testObjectSearcher.testObjectItemStatus = TestObjectItemStatus.NOT_SIGNED;

        List<TestObject> testObjects = method.apply(of(new Page<>(1, Page.DEFAULT_PAGE_SIZE)), testObjectSearcher);
        assert testObjects.size() == Page.DEFAULT_PAGE_SIZE;

        Page<TestObject> page = new Page<>(4, Page.DEFAULT_PAGE_SIZE);
        testObjects = method.apply(of(page), testObjectSearcher);
        System.out.println(testObjects.size());
        assert testObjects.size() == 5;
        assert page.getResult().size() == testObjects.size();
        assert page.getTotalCount() == 50;

        testObjectSearcher.testObjectItemStatus = TestObjectItemStatus.SIGNED;
        page = new Page<>(1, Page.DEFAULT_PAGE_SIZE);
        testObjects = method.apply(of(page), testObjectSearcher);
        assert testObjects.size() == 0;
        assert page.getResult().size() == testObjects.size();
        assert page.getTotalCount() == 0;

    }

}
