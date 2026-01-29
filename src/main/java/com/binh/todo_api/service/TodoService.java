package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.dto.TodoPatchRequest;
import com.binh.todo_api.dto.TodoUpdateRequest;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ConflicException;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.binh.todo_api.repository.TodoRepository;
import com.binh.todo_api.spec.TodoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {
    private final TodoJpaRepository todoRepository;
    public TodoService(TodoJpaRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    public List<TodoEntity> findAll(){
        return todoRepository.findAll();
    }
    public TodoEntity findById(long id){
        return todoRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Todo not found: " + id)
        );}
    public TodoEntity createTodo(TodoCreateRequest request){
        Optional<String> description = Optional.ofNullable(request.getDescription());
        TodoEntity todo = new TodoEntity(request.getTitle(), request.isCompleted(), description.orElse(""), request.getPriority());
        todoRepository.save(todo);
        return todo;
    }
    public void deleteTodo(long id){
        todoRepository.delete(todoRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Todo not found: " + id)
        ));
    }

    public TodoEntity updatePutTodo(long id, TodoUpdateRequest request){
        TodoEntity curr = this.findById(id);
        if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
            throw  new ConflicException("Can not change title after todo is completed");
        }
        boolean completed =  request.isCompleted();
        TodoEntity newTodo = curr.with(request.getTitle(), completed, request.getDescription(), request.getPriority());

        return this.todoRepository.save(newTodo);
    }

    public TodoEntity updatePatch(long id, TodoPatchRequest request){
        TodoEntity curr = this.findById(id);
        if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
            throw new ConflicException("Can not change title after todo is completed");
        }
        String newTitle = request.getTitle() == null ? curr.getTitle() : request.getTitle();
        boolean completed = request.isCompleted();
        String descrption = request.getDescription() == null ? curr.getDescription() : request.getDescription();
        Integer priority = request.getPriority() == null ? curr.getPriority() : request.getPriority();

        TodoEntity updated = curr.with(newTitle, completed, descrption, priority);
        return this.todoRepository.save(updated);
    }

    public void deleteAll(){
        this.todoRepository.deleteAll();
    }

    public void delete(long id){
        TodoEntity todo = this.findById(id);
        if(todo.isCompleted() == true){
            throw new ConflicException("Cannot delete this todo because its has been done ");
        }
        if(todoRepository.existsById(id)){
            todoRepository.deleteById(id);
        }else{
            throw new NotFoundException("Todo not found: " + id);
        }
    }

    public Page<TodoEntity> list(Boolean completed, String title,Integer minPriority, Integer maxPriority,String prefix, Pageable pageable){
        Specification<TodoEntity> spec = Specification.where(TodoSpecifications.titleContains(title))
                .and(TodoSpecifications.hasCompleted(completed)).and(TodoSpecifications.priorityRange(minPriority, maxPriority)).and(TodoSpecifications.startWithTitle(prefix));
        return todoRepository.findAll(spec, pageable);
    }
}

