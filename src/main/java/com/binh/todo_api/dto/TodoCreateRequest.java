package com.binh.todo_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;


@Schema(description = "Todo Entity representing a task item")
public class TodoCreateRequest {

    @Schema(description = "Title of the todo item", example = "Buy groceries")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Column(nullable = false)
    private boolean completed;
   @Size(max = 200, message = "Description must not exceed 200 characters")
    @Schema(description = "Detailed description of the todo item", example = "Buy milk, eggs, and bread from the supermarket")
    private String description;
    @Column(nullable = false)
    @NotNull(message = "Priority must not be null")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    @Schema(description = "Priority level of the todo item", example = "1")
    private int priority;


    public TodoCreateRequest(String title, boolean completed, String description, int priority) {
        this.title = title;
        this.completed = completed;
        this.description = description;
        this.priority = priority;
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
