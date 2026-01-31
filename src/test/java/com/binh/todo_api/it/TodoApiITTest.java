package com.binh.todo_api.it;

import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.repository.TodoJpaRepository; // Đảm bảo bạn đã import Repo
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// chỉ nên để cái này trong intergration test thôi, unit test không nên dùng
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TodoApiITTest extends ITBase {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TodoJpaRepository repository; // Dùng để dọn dẹp DB

    @BeforeEach
    void cleanUp() {
        repository.deleteAll(); // Đảm bảo môi trường sạch trước mỗi bài test
    }

    private TodoEntity create(String title, int priority) throws Exception {
        MvcResult result = mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "completed": false,
                                  "description": "d",
                                  "priority": %d
                                }
                                """.formatted(title, priority)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        // Lấy ID thật từ DB trả về thay vì hard-code số 1
        Number id = JsonPath.read(responseJson, "$.id");

        TodoEntity todo = new TodoEntity();
        todo.setId(id.longValue());
        todo.setTitle(title);
        todo.setPriority(priority);
        return todo;
    }

    @Test
    void listTodos_paging_ok() throws Exception {
        create("A", 1);
        create("B", 3);

        // Chú ý: $[0] sẽ là "B" vì sort priority,desc (3 > 1)
        mvc.perform(get("/api/todos")
                        .param("completed", "false")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "priority,desc")
                        .param("minPriority", "1")
                        .param("maxPriority", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].title").value("B"));
    }

    @Test
    void get_list_paging_check_fields() throws Exception {
        create("A", 1);

        mvc.perform(get("/api/todos")
                        .param("completed", "false")
                        .param("size", "1")
                        .param("sort", "id,desc")
                        .param("minPriority", "1")
                        .param("maxPriority", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value("A"))
                .andExpect(jsonPath("$.items[0].completed").value(false))
                .andExpect(jsonPath("$.items[0].priority").value(1));
    }

    @Test
    void patch_should_update_only_provided_fields() throws Exception {
        // Arrange
        TodoEntity todo = create("A", 1);
        long id = todo.getId();

        // Act & Assert
        mvc.perform(patch("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"completed": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.title").value("A")); // Title không được đổi
    }

    @Test
    void not_found_should_return_404() throws Exception {
        mvc.perform(get("/api/todos/99999999"))
                .andExpect(status().isNotFound()); // Dùng isNotFound() tường minh hơn
    }

    // Các test case khác giữ nguyên logic nhưng nên dùng isBadRequest() hoặc status().isOk()...
}