package com.binh.todo_api.repository;

import com.binh.todo_api.entity.TodoEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
@Repository
public interface TodoJpaRepository extends JpaRepository<TodoEntity, Long>, JpaSpecificationExecutor<TodoEntity> {

}
