package net.trenkmann.dev.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import net.trenkmann.dev.repository.source.SourceReproPackageMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

/**
 * DataConfiguration for {@code EntityManagerFactoryBean} and {@code TransactionManager} for source
 * database
 *
 * @author andreastrenkmann
 */
@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories(
    basePackageClasses = SourceReproPackageMarker.class,
    entityManagerFactoryRef = "sourceEntityManagerFactory",
    transactionManagerRef = "sourceTransactionManager")
public class SourceDataConfiguration {

  @Autowired private Environment env;

  @Primary
  @Bean("sourceEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean sourceEntityManagerFactory() {

    EclipseLinkJpaVendorAdapter eclipseAdapter = new EclipseLinkJpaVendorAdapter();
    eclipseAdapter.setDatabase(Database.MYSQL);
    eclipseAdapter.setGenerateDdl(true);
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPersistenceUnitName("source");
    em.setPackagesToScan("net.trenkmann.dev.model.source", "net.trenkmann.dev.model.jpa.converter");
    em.setJpaVendorAdapter(eclipseAdapter);
    em.setJpaProperties(additionalProperties());

    return em;
  }

  private Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty(
        "hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
    properties.setProperty(
        "hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
    properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
    properties.setProperty(
        "hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
    properties.setProperty("eclipselink.weaving", env.getProperty("eclipselink.weaving"));
    return properties;
  }

  @Bean("sourceDatasource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl(env.getProperty("spring.datasource.url"));
    dataSource.setUsername(env.getProperty("spring.datasource.username"));
    dataSource.setPassword(env.getProperty("spring.datasource.password"));
    return dataSource;
  }

  @Primary
  @Bean("sourceTransactionManager")
  public PlatformTransactionManager sourceTransactionManager(
      @Qualifier("sourceEntityManagerFactory") EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }
}
