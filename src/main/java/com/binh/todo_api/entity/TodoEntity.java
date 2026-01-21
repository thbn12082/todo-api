package com.binh.todo_api.entity;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "todos")
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length =  100)
    private String title;
    @Column(nullable = false)
    private boolean completed;
    @Column(length = 200)
    private String description;
    @Column(nullable = false)
    private int priority;

    public TodoEntity() {
    }

    public TodoEntity(long id, String title, boolean completed, String description, int priority) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.description = description;
        this.priority = priority;
    }

    public TodoEntity with (String title, boolean completed, String description, int priority){
        return new TodoEntity(this.id, title, completed, description, priority);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
