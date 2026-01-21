package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.dto.TodoPatchRequest;
import com.binh.todo_api.dto.TodoUpdateRequest;
import com.binh.todo_api.error.ConflicException;
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

    public Todo updatePutTodo(long id, TodoUpdateRequest request){
        Todo curr = this.findById(id);
        if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
            throw  new ConflicException("Can not change title after todo is completed");
        }
        boolean completed =  request.isCompleted();
        Todo newTodo = curr.with(request.getTitle(), completed, request.getDescription(), request.getPriority());

        return this.todoRepository.save(newTodo);
    }

    public Todo updatePatch(long id, TodoPatchRequest request){
        Todo curr = this.findById(id);
        if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
            throw new ConflicException("Can not change title after todo is completed");
        }
        String newTitle = request.getTitle() == null ? curr.getTitle() : request.getTitle();
        boolean completed = request.isCompleted();
        String descrption = request.getDescription() == null ? curr.getDescrption() : request.getDescription();
        Integer priority = request.getPriority() == null ? curr.getPriority() : request.getPriority();

        Todo updated = curr.with(newTitle, completed, descrption, priority);
        return this.todoRepository.save(updated);
    }

    public void delete(long id){
        if(todoRepository.exitsById(id)){
            todoRepository.delete(id);
        }else{
            throw new NotFoundException("Todo not found: " + id);
        }

    }
}

