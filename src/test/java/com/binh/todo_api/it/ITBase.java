package com.binh.todo_api.it;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class ITBase {

    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("todo")
                    .withUsername("todo")
                    .withPassword("todo");
    @BeforeAll
    static void start(){
        postgres.start();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r){
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        // Flyway bật để tạo schema trong test
        r.add("spring.flyway.enabled", () -> "true");

        // Hibernate chỉ validate, không tự tạo
        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}
