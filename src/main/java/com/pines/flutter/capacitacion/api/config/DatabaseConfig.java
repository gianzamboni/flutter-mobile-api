package com.pines.flutter.capacitacion.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "PGHOST")
public class DatabaseConfig {

    @Value("${PGHOST:localhost}")
    private String host;

    @Value("${PGPORT:5432}")
    private String port;

    @Value("${PGDATABASE:flutter-capacitacion}")
    private String database;

    @Value("${PGUSER:postgres}")
    private String username;

    @Value("${PGPASSWORD:postgres}")
    private String password;

    @Bean
    public DataSource dataSource() {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        
        return org.springframework.boot.jdbc.DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
