package com.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class BatchDBConfig {

    @Value("${business.db.url}")
    private String dbUrl;

    @Value("${business.db.user_name}")
    private String dbUserName;
    
    @Value("${business.db.password}")
    private String dbPassword;

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
