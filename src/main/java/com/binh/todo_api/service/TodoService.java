package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.dto.TodoPatchRequest;
import com.binh.todo_api.dto.TodoUpdateRequest;
import com.binh.todo_api.entity.AuditLogsEntity;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.ConflictException;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.error.PreconditionFailedException;
import com.binh.todo_api.error.PreconditionRequiredException;
import com.binh.todo_api.http.ETags;
import com.binh.todo_api.repository.AuditLogsRepository;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.binh.todo_api.repository.TodoRepository;
import com.binh.todo_api.spec.TodoSpecifications;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.JsonNode;

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


    @Transactional
    public TodoEntity patch(long id, JsonNode body, String ifMatch) throws BadRequestException {
        TodoEntity e = findById(id);
        String currentEtag = ETags.todo(e.getId(), e.getVersion());

        // 1) Precondition check (If-Match preferred)
        if (ifMatch != null && !ifMatch.isBlank()) {
            if (!ETags.matches(ifMatch, currentEtag)) {
                throw new PreconditionFailedException("ETag mismatch. Reload and retry.");
            }
        } else {
            // fallback: accept body.version (for backward compatibility)
            if (body == null || !body.has("version") || body.get("version").isNull()) {
                throw new PreconditionRequiredException("Missing If-Match header (or 'version' in body).");
            }
            long reqVersion = body.get("version").asLong();
            if (reqVersion != e.getVersion()) {
                throw new PreconditionFailedException("Stale version. Reload and retry.");
            }
        }

        // 2) Apply patch (absent vs null)
        if (body.has("title")) {
            if (body.get("title").isNull()) throw new BadRequestException("title cannot be null");
            String title = body.get("title").asText();
            if (title.isBlank()) throw new BadRequestException("title must not be blank");
            e.setTitle(title);
        }

        if (body.has("completed")) {
            if (body.get("completed").isNull()) throw new BadRequestException("completed cannot be null");
            e.setCompleted(body.get("completed").asBoolean());
        }

        if (body.has("priority")) {
            if (body.get("priority").isNull()) throw new BadRequestException("priority cannot be null");
            int p = body.get("priority").asInt();
            if (p < 1) throw new BadRequestException("priority must be >= 1");
            e.setPriority(p);
        }

        if (body.has("description")) {
            if (body.get("description").isNull()) {
                e.setDescription(null);
            } else {
                String d = body.get("description").asText();
                if (d.length() > 200) throw new BadRequestException("description must not exceed 200 characters");
                e.setDescription(d);
            }
        }

        return todoRepository.save(e);
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
    public TodoEntity update(long id,  TodoUpdateRequest request){
        TodoEntity e = todoRepository.findById(id).orElseThrow(() -> new NotFoundException("Todo not found"));

        if(e.getVersion() != request.getVersion()){
            throw new ConflictException("Stale version. Please reload and retry.");
        }

        e.setTitle(request.getTitle());
        e.setCompleted(request.isCompleted());
        e.setDescription(request.getDescription());
        e.setPriority(request.getPriority());
        return todoRepository.save(e);
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
            throw new ConflictException("Cannot delete this todo because its has been done ");
        }
        if(todoRepository.existsById(id)){
            todoRepository.deleteById(id);
        }else{
            throw new NotFoundException("Todo not found: " + id);
        }
    }
//@Transactional(readOnly = true)
    public Page<TodoEntity> list(Boolean completed, String title,Integer minPriority, Integer maxPriority,String prefix, Pageable pageable){
        Specification<TodoEntity> spec = Specification.where(TodoSpecifications.titleContains(title))
                .and(TodoSpecifications.hasCompleted(completed)).and(TodoSpecifications.priorityRange(minPriority, maxPriority)).and(TodoSpecifications.startWithTitle(prefix));
        return todoRepository.findAll(spec, pageable);
    }
}

