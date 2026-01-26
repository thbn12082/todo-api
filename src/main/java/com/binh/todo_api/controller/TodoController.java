package com.binh.todo_api.controller;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.*;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ApiError;
import com.binh.todo_api.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/todos")
public class TodoController {


    private final TodoService service;
    public TodoController(TodoService service){
        this.service = service;
    }

    private final Set<String> ALLOWED_SORT = Set.of("id", "title", "completed", "priority");

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
    public ResponseEntity<Object> list(@RequestParam(required = false) Boolean completed,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(defaultValue = "id,desc") String sort,
                                       @RequestParam(required = false) Integer minPriority,
                                       @RequestParam(required = false) Integer maxPriority,
                                       @RequestParam(required = false) String prefix) {
        if(size > 100){
            throw new IllegalArgumentException("size must be at most 100");
        }
        if( title != null &&!title.isBlank() && title.length() > 100){
            return ResponseEntity.status(400).body(new ApiError(Instant.now(), 400, "Title Too Long",  "/api/todos" , Map.of("title", "Title must be at most 100 characters long")));
        }
        if(minPriority > maxPriority){
            throw new IllegalArgumentException("minPriority must be less than or equal to maxPriority");
        }
        Sort springSort = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, springSort);
        Page<TodoEntity> result = service.list(completed, title, minPriority, maxPriority, prefix,  pageable);
        var todoResponses = result.getContent().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new TodoPageResponse<>(todoResponses, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages()));
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
    public Sort parseSort(String sort){
        String[] words = sort.split(";");
        Sort finalSort = Sort.by(Sort.Order.desc("id"));
        for(String word: words ){
            String[] parts = word.split(",");
            String field = parts[0].trim();
            String direction = parts[1].trim();
            if(!ALLOWED_SORT.contains(field.toLowerCase())){
                throw new IllegalArgumentException("Invalid sort field: " + field);
            }
            Sort.Order order = direction.equalsIgnoreCase("desc") ? Sort.Order.desc(field) : Sort.Order.asc(field);
            finalSort =  Sort.by(order);
        }
        return finalSort;

    }
}
