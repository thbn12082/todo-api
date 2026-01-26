package com.binh.todo_api.dev;

import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.repo.TodoJpaRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevSeed {

    @Bean
    ApplicationRunner seedTodos(TodoJpaRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            repo.save(new TodoEntity("Learn Flyway", false, "V1 migration + history table", 2));
            repo.save(new TodoEntity("Add profile config", true, "dev/prod split", 3));
            repo.save(new TodoEntity("Test pagination", false, "Need many rows later", 1));
        };
    }
}
