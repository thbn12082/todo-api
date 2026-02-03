package com.binh.todo_api.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class AuditLogsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String action;
    @Column(nullable = false, name = "reference_id")
    private Long referenceId;

    public AuditLogsEntity() {
    }

    public AuditLogsEntity(String action, Long referenceId) {
        this.action = action;
        this.referenceId = referenceId;
    }

    public Long getId(){
        return this.id;
    }

}
