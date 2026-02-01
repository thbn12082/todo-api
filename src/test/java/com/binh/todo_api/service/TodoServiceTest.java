package com.binh.todo_api.service;

import com.binh.todo_api.domain.Todo;
import com.binh.todo_api.dto.TodoCreateRequest;
import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.error.NotFoundException;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.binh.todo_api.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoServiceTest {
    TodoJpaRepository repo = mock(TodoJpaRepository.class);
    TodoService service = new TodoService(repo);

    @Test
    void create_shouldSavedEntity(){
        // arrange- chuẩn bị : dòng này để chuẩn bị test, giả lập hành vi save của repo, có trả về đối tượng chư skhoong trả về null
        TodoCreateRequest request = new TodoCreateRequest("Test create", false, "description", 3);
//        repo save đối tượng nào thì phải viết ở đây đúng đối tượng ấy, chứ không phải servcie nhận đối tượng nào đâu nhé
        when(repo.save(any(TodoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        //act- hành động
        TodoEntity saved = service.createTodo(request);
        //assert- xác nhận
        ArgumentCaptor<TodoEntity> cap = ArgumentCaptor.forClass(TodoEntity.class);
        verify(repo).save(cap.capture());

        assertEquals("Test create", saved.getTitle());
        assertEquals("description", saved.getDescription());
        assertEquals(3, saved.getPriority());
    }

    @Test
    void update_should_not_insert_new (){
        //arrange - chuẩn bị
        TodoEntity exiting = new TodoEntity("title",true, "description",2 );
        exiting.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(exiting));
        TodoEntity updated = exiting.with("updated title", false, "updated description", 5);
        //không được lưu bất kỳ đối tượng mới nào ở tầng repo
        verify(repo, never()).save(any());
        assertEquals(1, updated.getId());
        // kiểm tra xem exiting có bị thay đổi hay không mà không tạo ra 1 đối tượng mới
        assertEquals("updated title", exiting.getTitle());
    }

    @Test
    void not_found_should_throw_exception(){
        //assert, act
        assertThrows(NotFoundException.class, () -> {
            service.findById(99999L);
        });
    }


}
