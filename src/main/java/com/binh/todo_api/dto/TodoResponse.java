package com.binh.todo_api.dto;

public class TodoResponse {
    private String id;
    private String title;
    private boolean completed;
    private String description;
    private int priority;

    public TodoResponse(String id, String title, boolean completed, String description, int priority) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.description = description;
        this.priority = priority;
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
