package com.binh.todo_api;

import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.binh.todo_api.repository.TodoRepository;
import com.binh.todo_api.service.TodoService;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TodoServiceDeleteTest {
//    @Test
//    void delete_should_delete_todo_when_todo_exists(){
//        TodoJpaRepository repo = new TodoJpaRepository();
//        TodoService service = new TodoService(repo);
//
//        TodoCreateRequest request = new TodoCreateRequest();
//        request.setTitle("title");
//        request.setCompleted(true);
//        request.setDescription("description");
//        request.setPriority(3);
//        long id = service.createTodo(request).getId();
//        service.delete(id);
//
//        assertThrows(NotFoundException.class, () -> service.findById(id));
//
//
//    }
//
//    @Test
//    void delete_must_be_throw_not_found_exception_when_todo_not_exists(){
//        TodoRepository repo = new TodoRepository();
//        TodoService service = new TodoService(repo);
//
//        assertThrows(NotFoundException.class, () -> service.findById(1L));
//    }
//
//    @Test
//    void delete_should_not_throw_not_found_exception_when_deleting_all_todo(){
//        TodoRepository repo = new TodoRepository();
//        TodoService service = new TodoService(repo);
//
//        Assertions.assertDoesNotThrow(() -> service.deleteAll());
//    }
}
