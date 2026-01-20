package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final AtomicLong seq = new AtomicLong(0);
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    public List<Todo> findAll(){
        return todoRepository.findAll();
    }
    public Todo findById(long id){
        return todoRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Todo not found: " + id)
        );}
    public Todo createTodo(TodoCreateRequest request){
        Long id = seq.incrementAndGet();
        Optional<String> description = Optional.ofNullable(request.getDescription());
        Todo todo = new Todo(id, request.getTitle(), request.isCompleted(), description.orElse(""), request.getPriority());
        todoRepository.save(todo);
        return todo;
    }
    public void deleteTodo(long id){
        todoRepository.delete(id);
    }
}

