package org.shoppingcart.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.shoppingcart.dao.AccountDAO;
import org.shoppingcart.dao.OrderDAO;
import org.shoppingcart.dao.ProductDAO;
import org.shoppingcart.dao.impl.AccountDAOImpl;
import org.shoppingcart.dao.impl.OrderDAOImpl;
import org.shoppingcart.dao.impl.ProductDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * ApplicationContextConfig class used to configure Spring MVC Context,
 * including:
 * <ul>
 * <li>View Resolver</li>
 * <li>Datasouce</li>
 * <li>Hiberante (Hibernate Transaction Manager, Hibernate Session)</li>
 * <li>DAO</li>
 * <li>Bean</li>
 * </ul>
 * ApplicationContextConfig will read the Datasource configuration information
 * and Hibernate Properties in hibernate-cfg.properties file.
 * 
 * @author Krishna Gehlot R
 */
@Configuration
@ComponentScan("org.shoppingcart")
@EnableTransactionManagement
// Load to Environment
@PropertySource("classpath:hibernate-cfg.properties")
public class ApplicationContextConfig {

	private static final Logger LOGEVENT = Logger.getLogger(ApplicationContextConfig.class);

	// The Environment class servers as the property header and stores all the
	// properties loaded by the @PropertySource
	@Autowired
	private Environment environment;

	@Bean
	public ResourceBundleMessageSource getMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

		// Load property in message/validator.properties
		messageSource.setBasenames(new String[] { "messages/validator" });
		LOGEVENT.info("ApplicationContextConfig -> getMessageSource -> " + messageSource);
		return messageSource;
	}

	@Bean(name = "viewResolver")
	public InternalResourceViewResolver getViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		LOGEVENT.info("ApplicationContextConfig -> getViewResolver -> " + viewResolver);
		return viewResolver;
	}

	// Config for upload
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getMultipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(52428800);// Set maximum upload size
		LOGEVENT.info("ApplicationContextConfig -> getMultipartResolver -> " + multipartResolver);
		return multipartResolver;
	}

	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		// Set MYSQL database server properties
		dataSource.setDriverClassName(environment.getProperty("database.driver"));
		dataSource.setUrl(environment.getProperty("database.url"));
		dataSource.setUsername(environment.getProperty("database.username"));
		dataSource.setPassword(environment.getProperty("database.password"));
		LOGEVENT.info("ApplicationContextConfig -> getDataSource -> " + dataSource);
		return dataSource;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) throws IOException {
		Properties hibernateProperties = new Properties();

		// Set hibernate properties
		hibernateProperties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		hibernateProperties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
		hibernateProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		hibernateProperties.put("current_session_context_class",
				environment.getProperty("current_session_context_class"));
		hibernateProperties.put("hibernate.format.sql", environment.getProperty("hibernate.format.sql"));
		hibernateProperties.put("hibernate.use.sql.comments", environment.getProperty("hibernate.use.sql.comments"));

		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

		// Package contain entity classes
		factoryBean.setPackagesToScan(new String[] { "org.shoppingcart.entity" });
		factoryBean.setDataSource(dataSource);
		factoryBean.setHibernateProperties(hibernateProperties);
		factoryBean.afterPropertiesSet();

		// Instantiate SessionFactory object
		SessionFactory sessionFactory = factoryBean.getObject();
		LOGEVENT.info("ApplicationContextConfig -> getSessionFactory -> " + sessionFactory);
		return sessionFactory;
	}

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		LOGEVENT.info("ApplicationContextConfig -> getTransactionManager -> " + transactionManager);
		return transactionManager;
	}

	@Bean(name = "accountDAO")
	public AccountDAO getAccountDAO() {
		LOGEVENT.info("ApplicationContextConfig -> getAccountDAO");
		return new AccountDAOImpl();
	}

	@Bean(name = "productDAO")
	public ProductDAO getProductDAO() {
		LOGEVENT.info("ApplicationContextConfig -> getProductDAO");
		return new ProductDAOImpl();
	}

	@Bean(name = "orderDAO")
	public OrderDAO getOrderDAO() {
		LOGEVENT.info("ApplicationContextConfig -> getOrderDAO");
		return new OrderDAOImpl();
	}
}