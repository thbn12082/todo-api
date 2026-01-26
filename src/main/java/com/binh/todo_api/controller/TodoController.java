package com.binh.todo_api.controller;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.*;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ApiError;
import com.binh.todo_api.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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


    private final TodoService service;
    public TodoController(TodoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> create (@Valid @RequestBody TodoCreateRequest request){

        boolean completed = request.isCompleted();
        if(completed == true && request.getTitle().length() < 5){
            return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Title Too Short",  "/api/todos" , Map.of("title", "Title must be at least 5 characters long if completed is true")));
        }
       TodoEntity todo =  service.createTodo(request);
        return ResponseEntity.created(URI.create("/todos/" + todo.getId())).body(todo);
    }

    @GetMapping
    public ResponseEntity<Object> list(@RequestParam(required = false) Boolean completed, @RequestParam(required = false) String title, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
    Pageable pageable, @RequestParam(required = false) Integer minPriority, @RequestParam(required = false) Integer maxPriority, @RequestParam(required = false) String prefix) {
        if(pageable.getPageSize() > 100){
            return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Page Size Too Large",  "/api/todos" , Map.of("pageSize", "Page size must be at most 100")));
           }

       if( title != null &&!title.isBlank() && title.length() > 100){
            return ResponseEntity.status(400).body(new ApiError(Instant.now(), 400, "Title Too Long",  "/api/todos" , Map.of("title", "Title must be at most 100 characters long")));
        }
        Page<TodoEntity> page = service.list(completed, title, minPriority, maxPriority, prefix,  pageable);
        var todoResponses = page.getContent().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new TodoPageResponse<>(todoResponses, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
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


    public TodoResponse toResponse(TodoEntity todo){
        return new TodoResponse(String.valueOf(todo.getId()), todo.getTitle(), todo.isCompleted(), todo.getDescription(), todo.getPriority());
    }

}
