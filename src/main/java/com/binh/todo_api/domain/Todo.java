package com.binh.todo_api.domain;

public class Todo {
    private long id;
    private String title;
    private boolean completed;
    private String descrption;
    private int priority = 3;

    public Todo(long id, String title, boolean completed, String descrption, int priority) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.descrption = descrption;
        this.priority = priority;
    }

    public Todo with (String title, boolean completed, String descrption, int priority){
        return new Todo(this.id, title, completed, descrption, priority);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDescrption() {
        return descrption;
    }

    public int getPriority() {
        return priority;
    }
}
