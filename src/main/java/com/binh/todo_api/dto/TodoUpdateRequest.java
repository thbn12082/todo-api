package com.binh.todo_api.dto;

import jakarta.validation.constraints.*;

public class TodoUpdateRequest {
    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
    private boolean completed = false;
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    @NotNull(message = "Priority must not be null")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    private int priority;
@NotNull Long version;




    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
