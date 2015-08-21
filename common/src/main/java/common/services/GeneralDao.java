package common.services;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.DateUtils;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.utils.SQLUtils.*;

/**
 * Created by liubin on 15-4-3.
 */
@Repository
public class GeneralDao {

    @PersistenceContext
    EntityManager em;

    public GeneralDao(){}

    public GeneralDao(EntityManager em) {this.em = em;}

    public EntityManager getEm() {
        return em;
    }

    /**
     * 使用jpql进行查询
     * @param ql jpql
     * @param page 分页对象,可选
     * @param queryParams 查询参数
     * @param <T>
     * @return
     */
    public <T> List<T> query(String ql, Optional<Page<T>> page, Map<String, Object> queryParams) {

        Query query = em.createQuery(ql);
        queryParams.forEach(query::setParameter);

        if(page.isPresent()) {

            String countQl = " select count(1) " + removeFetchInCountQl(removeSelect(removeOrderBy(ql)));
            Query countQuery = em.createQuery(countQl);
            queryParams.forEach(countQuery::setParameter);

            if(hasGroupBy(ql)) {
                List resultList = countQuery.getResultList();
                page.get().setTotalCount(resultList.size());

            } else {
                Long count = (Long)countQuery.getSingleResult();
                page.get().setTotalCount(count.intValue());

            }
            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());
        }

        List<T> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    /**
     * 查询所有
     * @param type
     * @param <T>
     * @return
     */
    public <T extends EntityClass<Integer>> List<T> findAll(Class<T> type) {
        CriteriaQuery<T> c = em.getCriteriaBuilder().createQuery(type);
        c.from(type);
        return em.createQuery(c).getResultList();
    }

    /**
     * 使用jpql进行数据更新操作
     * @param ql
     * @param queryParams
     * @return
     */
    public int update(String ql, Map<String, Object> queryParams) {

        Query query = em.createQuery(ql);
        queryParams.forEach(query::setParameter);

        return query.executeUpdate();
    }

    public <T extends EntityClass<Integer>> void persist(T t) {
        setOperableDataIfNecessary(t, t.getId() == null || t.getId() == 0);
        em.persist(t);
    }

    public <T extends EntityClass<Integer>> T merge(T t) {
        setOperableDataIfNecessary(t, t.getId() == null || t.getId() == 0);
        return em.merge(t);
    }

    public <T extends EntityClass<Integer>> boolean remove(T t) {
        if(t != null) {
            em.remove(t);
            return true;
        } else {
            return false;
        }
    }

    public <T extends EntityClass<Integer>> boolean removeById(Class<T> type, Integer id) {
        T t = get(type, id);
        return remove(t);
    }

    public <T extends EntityClass<Integer>> T get(Class<T> type, Integer id) {
        return em.find(type, id);
    }

    public void flush() {
        em.flush();
    }

    public <T extends EntityClass<Integer>> void refresh(T t) {
        em.refresh(t);
    }

    public <T extends EntityClass<Integer>> void detach(T t) {
        em.detach(t);
    }



    private <T extends EntityClass<Integer>> void setOperableDataIfNecessary(T t, boolean isCreate) {
        if(t instanceof OperableData) {
            OperableData operableData = (OperableData)t;
            DateTime now = DateUtils.current();
            operableData.setUpdateTime(now);
            if(isCreate) {
                operableData.setCreateTime(now);
            }
            //TODO set operator
        }
    }


}
