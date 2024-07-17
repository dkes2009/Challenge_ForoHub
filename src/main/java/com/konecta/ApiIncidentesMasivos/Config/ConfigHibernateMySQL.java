package com.konecta.ApiIncidentesMasivos.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerMySql", transactionManagerRef = "transaccionManagerMySql", basePackages = {"com.konecta.ApiIncidentesMasivos.Repositorymsql"})
public class ConfigHibernateMySQL extends Throwable {
    @Bean(name = {"dataSourceMySql"})
    @ConfigurationProperties(prefix = "db2.datasource")
    public DataSource dataSourceMysql() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = {"entityManagerMySql"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryMYSQL(@Qualifier("dataSourceMySql") DataSource datasource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(datasource).packages(new String[]{"com.konecta.ApiIncidentesMasivos.Entitymysql"}).persistenceUnit("db2").build();
    }
    @Bean(name = {"transaccionManagerMySql"})
    public PlatformTransactionManager transaccionManagerMysql(@Qualifier("entityManagerMySql") EntityManagerFactory manager) {
        return (PlatformTransactionManager) new JpaTransactionManager(manager);
    }
}
