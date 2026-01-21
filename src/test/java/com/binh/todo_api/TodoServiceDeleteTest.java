package com.binh.todo_api;

import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.repository.TodoRepository;
import com.binh.todo_api.service.TodoService;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Test;

//@NotBlank(message = "Title must not be blank")
//@Size(max = 100, message = "Title must not exceed 100 characters")
//private String title;
//        private boolean completed = false;
//        @Size(max = 200, message = "Description must not exceed 200 characters")
//        private String description;
//        @NotNull(message = "Priority must not be null")
//        @Min(value = 1, message = "Priority must be at least 1")
//        @Max(value = 5, message = "Priority must be at most 5")
//        private int priority;


public class TodoServiceDeleteTest {
    @Test
    void delete_should_delete_todo_when_todo_exists(){
        TodoRepository repo = new TodoRepository();
        TodoService service = new TodoService(repo);

        TodoCreateRequest request = new TodoCreateRequest();
        request.setTitle("title");
        request.setCompleted(true);
        request.setDescription("description");
        request.setPriority(3);
        long id = service.createTodo(request).getId();



    }

    @Test
    void delete_must_be_throw_not_found_exception_when_todo_not_exists(){

    }
}
