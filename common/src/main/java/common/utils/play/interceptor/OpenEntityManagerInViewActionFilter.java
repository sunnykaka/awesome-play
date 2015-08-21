package common.utils.play.interceptor;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import play.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * Created by liubin on 15-4-14.
 */
public class OpenEntityManagerInViewActionFilter implements ActionFilter {

    final EntityManagerFactory emf;

    public OpenEntityManagerInViewActionFilter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void doFilter(ActionFilterChain filterChain) throws Throwable {
        boolean participate = false;
        if (TransactionSynchronizationManager.hasResource(emf)) {
            participate = true;
        } else {
//            Logger.debug("Opening single JPA EntityManager in Global.onRequest");
            try {
                EntityManager em = emf.createEntityManager();
                EntityManagerHolder emHolder = new EntityManagerHolder(em);
                TransactionSynchronizationManager.bindResource(emf, emHolder);
            } catch (PersistenceException ex) {
                throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
            }

        }

        try {

            filterChain.doFilter();

        } finally {

            if (!participate) {
                EntityManagerHolder emHolder = (EntityManagerHolder)
                TransactionSynchronizationManager.unbindResource(emf);
//                Logger.debug("Closing JPA EntityManager in Global.onRequest");
                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
            }
        }
    }



}
