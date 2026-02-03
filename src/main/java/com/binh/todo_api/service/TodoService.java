package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.dto.TodoPatchRequest;
import com.binh.todo_api.dto.TodoUpdateRequest;
import com.binh.todo_api.entity.AuditLogsEntity;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ConflicException;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.repository.AuditLogsRepository;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.binh.todo_api.repository.TodoRepository;
import com.binh.todo_api.spec.TodoSpecifications;
import org.springframework.transaction.annotation.Transactional;
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
    private final AuditLogsRepository auditLogsRepository;
    public TodoService(TodoJpaRepository todoRepository, AuditLogsRepository auditLogsRepository) {
        this.todoRepository = todoRepository;
        this.auditLogsRepository = auditLogsRepository;
    }
    @Transactional(readOnly = true)
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
@Transactional
    public TodoEntity updatePutTodo(long id, TodoUpdateRequest request){
        TodoEntity curr = this.findById(id);
        TodoEntity newTodo = curr.with(request.getTitle(), request.isCompleted(), request.getDescription(), request.getPriority());
        TodoEntity res = this.todoRepository.save(newTodo);
        auditLogsRepository.save(new AuditLogsEntity("UPDATE_PUT", curr.getId()));
        if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
            throw  new RuntimeException("Can not change title after todo is completed");
        }
        return res;
    }

@Transactional
    //RuntimeException → Spring rollback transaction.
    public TodoEntity createTodoWithAudit(TodoEntity todo){
        TodoEntity res = todoRepository.save(todo);
        auditLogsRepository.save(new AuditLogsEntity("CREATE_TODO", todo.getId()));
        if(todo.getTitle() == null || todo.getTitle().contains("FAIL")){
            throw new IllegalStateException("Simulated failure after creating todo");
        }
        return res;
    }
@Transactional
    public TodoEntity updatePatch(long id, TodoPatchRequest request){
        TodoEntity curr = this.findById(id);

        String newTitle = request.getTitle() == null ? curr.getTitle() : request.getTitle();
        boolean completed = request.isCompleted();
        String descrption = request.getDescription() == null ? curr.getDescription() : request.getDescription();
        Integer priority = request.getPriority() == null ? curr.getPriority() : request.getPriority();
    TodoEntity updated = curr.with(newTitle, completed, descrption, priority);
    this.todoRepository.save(updated);
    this.auditLogsRepository.save(new AuditLogsEntity("UPDATE_PATCH", curr.getId()));

    if(curr.isCompleted() && !curr.getTitle().equals(request.getTitle())){
        throw new RuntimeException("Can not change title after todo is completed");
    }

        return updated;
    }

    @Transactional
    public void deleteAll(){
        this.todoRepository.deleteAll();
        this.auditLogsRepository.deleteAll();
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
@Transactional(readOnly = true)
    public Page<TodoEntity> list(Boolean completed, String title,Integer minPriority, Integer maxPriority,String prefix, Pageable pageable){
        Specification<TodoEntity> spec = Specification.where(TodoSpecifications.titleContains(title))
                .and(TodoSpecifications.hasCompleted(completed)).and(TodoSpecifications.priorityRange(minPriority, maxPriority)).and(TodoSpecifications.startWithTitle(prefix));
        return todoRepository.findAll(spec, pageable);
    }
}

