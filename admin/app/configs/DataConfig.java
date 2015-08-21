package configs;

import common.utils.play.BaseGlobal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import play.db.Database;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
public class DataConfig {

    @Bean
    @Lazy
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setPackagesToScan("**.models");
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaPropertyMap(new HashMap<String, String>(){{
            put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
            put("hibernate.format_sql", "true");
            put("hibernate.show_sql", "false");
            put("hibernate.query.substitutions", "true 1, false 0");
            put("hibernate.default_batch_fetch_size", "50");
            put("hibernate.jdbc.batch_size", "50");
            put("hibernate.order_inserts", "true");
            put("hibernate.max_fetch_depth", "2");
            put("hibernate.current_session_context_class", "org.springframework.orm.hibernate4.SpringSessionContext");
            put("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
        }});
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    @Bean
    @Lazy
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory());

        return transactionManager;
    }

    @Bean
    @Lazy
    public DataSource dataSource(){
        return BaseGlobal.injector.instanceOf(Database.class).getDataSource();
    }


}