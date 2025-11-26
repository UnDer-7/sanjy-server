package br.com.gorillaroxo.sanjy.server.infrastructure.test.config;

import org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17-alpine");
        container.start();
        return container;
    }

    /**
     * Provides JdbcConnectionDetails bean for GraalVM native image tests.
     * This is necessary because @ServiceConnection doesn't work in AOT mode.
     */
    @Bean
    public JdbcConnectionDetails jdbcConnectionDetails(PostgreSQLContainer<?> container) {
        return new JdbcConnectionDetails() {
            @Override
            public String getJdbcUrl() {
                return container.getJdbcUrl();
            }

            @Override
            public String getUsername() {
                return container.getUsername();
            }

            @Override
            public String getPassword() {
                return container.getPassword();
            }
        };
    }

    /**
     * Provides JdbcConnectionDetails bean for GraalVM native image tests.
     * This is necessary because @ServiceConnection doesn't work in AOT mode.
     */
    @Bean
    public FlywayConnectionDetails flywayConnectionDetails(final PostgreSQLContainer<?> container) {
        return new FlywayConnectionDetails() {
            @Override
            public String getJdbcUrl() {
                return container.getJdbcUrl();
            }

            @Override
            public String getUsername() {
                return container.getUsername();
            }

            @Override
            public String getPassword() {
                return container.getPassword();
            }
        };
    }

}
