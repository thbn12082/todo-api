package com.binh.todo_api.spec;

import com.binh.todo_api.entity.TodoEntity;
import org.springframework.data.jpa.domain.Specification;

public class TodoSpecifications {

    public static Specification<TodoEntity> fieldIsNotNull(String fieldName){
        return (root, query, cb) -> cb.isNotNull(root.get(fieldName));
    }

    // lọc theo tên
    public static Specification<TodoEntity> titleContains(String title){
        return (root, query, cb) -> {
            if(title == null || title.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
        };
    }

    // lọc theo trạng thái hoàn thành
    public static Specification<TodoEntity> hasCompleted(Boolean completed){
        if(completed == null){
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("completed"), completed);
    }


    // theo khoảng độ ưu tiên
    public static Specification<TodoEntity> priorityRange(Integer minPriority, Integer maxPriority){
        return (root, query, cb) ->{
            if((minPriority != null) &&( maxPriority != null)){
                return cb.between(root.get("priority"), minPriority, maxPriority);
            } else if (minPriority != null) {
                return cb.greaterThanOrEqualTo(root.get("priority"), minPriority);
            } else if (maxPriority != null) {
                return cb.lessThanOrEqualTo(root.get("priority"), maxPriority);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<TodoEntity> startWithTitle(String prefix){
        if(prefix != null && !prefix.isBlank()){
            return (root, query, cb) -> cb.like(cb.lower(root.get("title")), prefix.toLowerCase() + "%");
        }
        return (root,query, cb) -> cb.conjunction();
    }


}
