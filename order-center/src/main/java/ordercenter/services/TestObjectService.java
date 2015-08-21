package ordercenter.services;

import common.services.GeneralDao;
import common.utils.IdUtils;
import common.utils.page.Page;
import ordercenter.constants.PlatformType;
import ordercenter.constants.TestObjectStatus;
import ordercenter.dtos.TestObjectSearcher;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

import static common.utils.SQLUtils.*;

/**
 * Created by liubin on 15-4-2.
 */
@Service
public class TestObjectService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDao;

    @Transactional(readOnly = true)
    public List<TestObject> findByKey(Optional<Page<TestObject>> page, Optional<String> orderNo, Optional<TestObjectStatus> status,
            Optional<DateTime> createTimeStart, Optional<DateTime> createTimeEnd) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TestObject> cq = cb.createQuery(TestObject.class);
        Root<TestObject> order = cq.from(TestObject.class);

        List<Predicate> predicateList = new ArrayList<>();
        if(orderNo.isPresent()) {
            predicateList.add(cb.equal(order.get("orderNo"), orderNo.get()));
        }
        if(createTimeStart.isPresent()) {
            predicateList.add(cb.greaterThanOrEqualTo(order.get("createTime"), createTimeStart.get()));
        }
        if(createTimeEnd.isPresent()) {
            predicateList.add(cb.lessThanOrEqualTo(order.get("createTime"), createTimeEnd.get()));
        }
        if(status.isPresent()) {
            predicateList.add(cb.equal(order.get("status"), status.get()));
        }

        cq.select(order).where(predicateList.toArray(new Predicate[predicateList.size()])).orderBy(cb.desc(order.get("updateTime")));

        TypedQuery<TestObject> query = em.createQuery(cq);

        if(page.isPresent()) {
            CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
            countCq.select(cb.count(countCq.from(TestObject.class))).where(predicateList.toArray(new Predicate[predicateList.size()]));
            Long count = em.createQuery(countCq).getSingleResult();
            page.get().setTotalCount(count.intValue());

            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());
        }

        List<TestObject> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<TestObject> findByComplicateKey(Optional<Page<TestObject>> page, TestObjectSearcher testObjectSearcher) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TestObject> cq = cb.createQuery(TestObject.class);
        Root<TestObject> order = cq.from(TestObject.class);
        ListJoin<TestObject, TestObjectItem> orderItemList = order.joinList("testObjectItemList");

        List<Predicate> predicateList = new ArrayList<>();
        if(testObjectSearcher.createTimeStart != null) {
            predicateList.add(cb.greaterThanOrEqualTo(order.get("createTime"), testObjectSearcher.createTimeStart));
        }
        if(testObjectSearcher.createTimeEnd != null) {
            predicateList.add(cb.lessThanOrEqualTo(order.get("createTime"), testObjectSearcher.createTimeEnd));
        }
        if(testObjectSearcher.status != null) {
            predicateList.add(cb.equal(order.get("status"), testObjectSearcher.status));
        }
        if(testObjectSearcher.testObjectItemStatus != null) {
            predicateList.add(cb.equal(orderItemList.get("status"), testObjectSearcher.testObjectItemStatus));
        }
        if(!StringUtils.isBlank(testObjectSearcher.productSku)) {
            predicateList.add(cb.equal(orderItemList.get("productSku"), testObjectSearcher.productSku));
        }

        cq.select(order).where(predicateList.toArray(new Predicate[predicateList.size()])).groupBy(order.get("id"));

        TypedQuery<TestObject> query = em.createQuery(cq);

        if(page.isPresent()) {

            CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
            countCq.from(TestObject.class).joinList("testObjectItemList");
            countCq.select(cb.count(order)).where(predicateList.toArray(new Predicate[predicateList.size()])).groupBy(order.get("id"));
            List resultList = em.createQuery(countCq).getResultList();
            page.get().setTotalCount(resultList.size());

            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());
        }

        List<TestObject> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<TestObject> findByComplicateKeyWithJpql(Optional<Page<TestObject>> page, TestObjectSearcher testObjectSearcher) {

        String jpql = "select o from TestObject o join o.testObjectItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(testObjectSearcher.orderNo != null) {
            jpql += " and o.orderNo = :orderNo ";
            queryParams.put("orderNo", testObjectSearcher.orderNo);
        }
        if(testObjectSearcher.createTimeStart != null) {
            jpql += " and o.createTime >= :createTimeStart ";
            queryParams.put("createTimeStart", testObjectSearcher.createTimeStart);
        }
        if(testObjectSearcher.createTimeEnd != null) {
            jpql += " and o.createTime <= :createTimeEnd ";
            queryParams.put("createTimeEnd", testObjectSearcher.createTimeEnd);
        }
        if(testObjectSearcher.status != null) {
            jpql += " and o.status = :status ";
            queryParams.put("status", testObjectSearcher.status);
        }
        if(testObjectSearcher.testObjectItemStatus != null) {
            jpql += " and oi.status = :testObjectItemStatus ";
            queryParams.put("testObjectItemStatus", testObjectSearcher.testObjectItemStatus);
        }
        if(!StringUtils.isBlank(testObjectSearcher.productSku)) {
            jpql += " and oi.productSku = :productSku ";
            queryParams.put("productSku", testObjectSearcher.productSku);
        }

        jpql += " group by o.id ";

        Query query = em.createQuery(jpql);
        queryParams.forEach(query::setParameter);

        if(page.isPresent()) {

            String countJpql = " select count(1) " + removeFetchInCountQl(removeSelect(removeOrderBy(jpql)));
            Query countQuery = em.createQuery(countJpql);
            queryParams.forEach(countQuery::setParameter);


            if(hasGroupBy(jpql)) {
                List resultList = countQuery.getResultList();
                page.get().setTotalCount(resultList.size());

            } else {

                Long count = (Long)countQuery.getSingleResult();
                page.get().setTotalCount(count.intValue());

            }
            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());

        }

        List<TestObject> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<TestObject> findByComplicateKeyWithGeneralDaoQuery(Optional<Page<TestObject>> page, TestObjectSearcher testObjectSearcher) {

        String jpql = "select o from TestObject o join o.testObjectItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(testObjectSearcher.orderNo != null) {
            jpql += " and o.orderNo = :orderNo ";
            queryParams.put("orderNo", testObjectSearcher.orderNo);
        }
        if(testObjectSearcher.createTimeStart != null) {
            jpql += " and o.createTime >= :createTimeStart ";
            queryParams.put("createTimeStart", testObjectSearcher.createTimeStart);
        }
        if(testObjectSearcher.createTimeEnd != null) {
            jpql += " and o.createTime <= :createTimeEnd ";
            queryParams.put("createTimeEnd", testObjectSearcher.createTimeEnd);
        }
        if(testObjectSearcher.status != null) {
            jpql += " and o.status = :status ";
            queryParams.put("status", testObjectSearcher.status);
        }
        if(testObjectSearcher.testObjectItemStatus != null) {
            jpql += " and oi.status = :testObjectItemStatus ";
            queryParams.put("testObjectItemStatus", testObjectSearcher.testObjectItemStatus);
        }
        if(!StringUtils.isBlank(testObjectSearcher.productSku)) {
            jpql += " and oi.productSku = :productSku ";
            queryParams.put("productSku", testObjectSearcher.productSku);
        }

        jpql += " group by o.id ";

        return generalDao.query(jpql, page, queryParams);

    }

    @Transactional
    public void saveTestObject(TestObject testObject) {
        if(IdUtils.isEmpty(testObject.getId())) {
            //ID为空,新增
            testObject.setPlatformType(PlatformType.WEB);
            testObject.setOrderNo(RandomStringUtils.randomAlphanumeric(8));
            generalDao.persist(testObject);

            for(TestObjectItem testObjectItem : testObject.getTestObjectItemList()) {
                testObjectItem.setTestObjectId(testObject.getId());
                generalDao.persist(testObjectItem);
            }

        } else {
            //修改
            generalDao.merge(testObject);

            for(TestObjectItem testObjectItem : testObject.getTestObjectItemList()) {
                if(IdUtils.isEmpty(testObjectItem.getId())) {
                    //ID为空,新增
                    testObjectItem.setTestObjectId(testObject.getId());
                    generalDao.persist(testObjectItem);
                } else {
                    //修改
                    generalDao.merge(testObjectItem);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public TestObject get(Integer id) {
        return generalDao.get(TestObject.class, id);
    }

}
