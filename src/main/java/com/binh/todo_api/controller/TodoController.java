package com.binh.todo_api.controller;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.dto.TodoPatchRequest;
import com.binh.todo_api.dto.TodoResponse;
import com.binh.todo_api.dto.TodoUpdateRequest;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ApiError;
import com.binh.todo_api.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
//    private List<TodoResponse> todos = new ArrayList<>();
    // id tự tăng
    private AtomicLong seq = new AtomicLong(0);

    private final TodoService service;
    public TodoController(TodoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> create (@Valid @RequestBody TodoCreateRequest request){
        long id = seq.incrementAndGet();
        boolean completed = request.isCompleted();
        if(completed == true && request.getTitle().length() < 5){
            return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Title Too Short",  "/api/todos" , Map.of("title", "Title must be at least 5 characters long if completed is true")));
        }


        TodoResponse res = new TodoResponse(String.valueOf(id), request.getTitle(), completed, request.getDescription(), request.getPriority());
        service.createTodo(request);
        return ResponseEntity.created(URI.create("/todos/" + id)).body(res);
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> lst(){
        List<TodoEntity> todos = service.findAll();
        List<TodoResponse> response = new ArrayList<>();
        todos.forEach(i ->{
            TodoResponse todoResponse = new TodoResponse(
                    String.valueOf(i.getId()),
                    i.getTitle(),
                    i.isCompleted(),
                    i.getDescription(),
                    i.getPriority()
            );
            response.add(todoResponse);
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable("id") long id){
        TodoEntity todo = service.findById(id);
        TodoResponse response = new TodoResponse(
                String.valueOf(todo.getId()),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getDescription(),
                todo.getPriority());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public Map<String, String> health(){
        return Map.of("status", "OK");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable long id,@Valid @RequestBody TodoUpdateRequest request){
        this.service.updatePutTodo(id, request);
        TodoResponse res = new TodoResponse(String.valueOf(id), request.getTitle(), request.isCompleted(), request.getDescription(), request.getPriority());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoResponse> updatePatch(@PathVariable long id, @Valid @RequestBody TodoPatchRequest request){
        TodoEntity todo = this.service.updatePatch(id, request);
        TodoResponse res = new TodoResponse(String.valueOf(id), todo.getTitle(), todo.isCompleted(), todo.getDescription(), todo.getPriority());
        return ResponseEntity.ok(res);

    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(){
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
