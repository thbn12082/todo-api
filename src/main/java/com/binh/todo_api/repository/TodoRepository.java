package com.binh.todo_api.repository;

import com.binh.todo_api.domain.Todo;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TodoRepository {
    private final Map<Long, Todo> storage = new HashMap<>();

    public List<Todo> findAll(){
        return new ArrayList<>(storage.values());
    }

    public Optional<Todo> findById(Long id){
        return Optional.ofNullable(storage.get(id));
    }

    public Todo save(Todo todo){
        storage.put(todo.getId(), todo);
        return todo;
    }

    public boolean exitsById(Long id){
        return storage.containsKey(id);
    }

    public void delete(Long id){
        storage.remove(id);
    }

}
