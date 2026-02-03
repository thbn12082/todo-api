package com.binh.todo_api.repository;

import com.binh.todo_api.entity.AuditLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogsRepository extends JpaRepository<AuditLogsEntity, Long> {

}