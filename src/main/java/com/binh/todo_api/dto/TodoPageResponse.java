package com.binh.todo_api.dto;

import java.util.List;

public class TodoPageResponse <T>{
    private List<T> items;
private int page;
private int size;
private long totalItems;
private long totalPages;
    public TodoPageResponse(List<T> items, int page, int size, long totalItems, long totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public long getTotalPages() {
        return totalPages;
    }
}
