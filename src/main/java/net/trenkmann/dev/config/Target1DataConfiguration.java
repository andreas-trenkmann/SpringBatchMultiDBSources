package net.trenkmann.dev.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import net.trenkmann.dev.repository.target1.Target1ReproPackageMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DataConfiguration for {@code EntityManagerFactoryBean} and {@code TransactionManager}
 * for first target database
 *
 * @author andreastrenkmann
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Target1ReproPackageMarker.class,
    entityManagerFactoryRef = "target1EMF",
    transactionManagerRef = "target1TransactionManager")
public class Target1DataConfiguration {

  @Autowired
  private Environment env;

  @Bean("target1EMF")
  public LocalContainerEntityManagerFactoryBean target1EntityManagerFactory() {

    EclipseLinkJpaVendorAdapter eclipseAdapter = new EclipseLinkJpaVendorAdapter();
    eclipseAdapter.setDatabase(Database.MYSQL);
    eclipseAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(target1DataSource());
    em.setPackagesToScan("net.trenkmann.dev.model.target1",
        "net.trenkmann.dev.model.jpa.converter");
    em.setPersistenceUnitName("target1");
    em.setJpaVendorAdapter(eclipseAdapter);
    em.setJpaProperties(additionalProperties());

    return em;
  }

  private Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto",
        env.getProperty("spring.jpa.hibernate.ddl-auto"));
    properties.setProperty("hibernate.dialect",
        env.getProperty("spring.jpa.properties.hibernate.dialect"));
    properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
    properties.setProperty("hibernate.format_sql",
        env.getProperty("spring.jpa.properties.hibernate.format_sql"));
    properties.setProperty("eclipselink.weaving", env.getProperty("eclipselink.weaving"));

    properties.setProperty("eclipselink.allow-zero-id", env.getProperty("eclipselink.allow-zero-id"));
    return properties;
  }

  @Primary
  @Bean("target1Datasource")
  public DataSource target1DataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl(env.getProperty("net.trenkmann.dev.model.target1.url"));
    dataSource.setUsername(env.getProperty("net.trenkmann.dev.model.target1.username"));
    dataSource.setPassword(env.getProperty("net.trenkmann.dev.model.target1.password"));
    return dataSource;
  }


  @Bean("target1TransactionManager")
  public PlatformTransactionManager target1TransactionManager(@Qualifier("target1EMF") EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }
}
