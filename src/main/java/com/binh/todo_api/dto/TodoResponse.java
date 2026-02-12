package com.binh.todo_api.dto;

import com.binh.todo_api.entity.TodoEntity;

public class TodoResponse {
    private String id;
    private String title;
    private boolean completed;
    private String description;
    private int priority;
    private long version;

    public static TodoResponse fromEntity(TodoEntity e){
        return new TodoResponse(String.valueOf(e.getId()), e.getTitle(), e.isCompleted(), e.getDescription(), e.getPriority(), e.getVersion());
    }
    public TodoResponse(String id, String title, boolean completed, String description, int priority) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.description = description;
        this.priority = priority;
    }

    public TodoResponse(String id, String title, boolean completed, String description, int priority, long version) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.description = description;
        this.priority = priority;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
