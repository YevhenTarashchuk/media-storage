package com.media_storage.auth_core.config;

import com.media_storage.communication_client.CommunicationClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

@TestConfiguration
@EnableTransactionManagement
@PropertySource("classpath:application-test.yml")
public class TestPersistenceConfig {

    @MockBean
    CommunicationClient communicationClient;

    @Value("${driver-class-name}")
    private String driverClassName;
    @Value("${postgresImageVersion}")
    private String postgresImageVersion;

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("core-test-hikari-config") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(initMethod = "start")
    public JdbcDatabaseContainer<?> postgresContainer() {
        return TestPostgresContainer.getInstance(postgresImageVersion);
    }

    @Bean
    @Primary
    @Qualifier("core-test-hikari-config")
    public HikariConfig hikariConfig(JdbcDatabaseContainer<?> postgresContainer) {
        HikariConfig config = new HikariConfig();
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setDriverClassName(driverClassName);

        return config;
    }
}