package com.binh.todo_api.it;

import com.binh.todo_api.entity.TodoEntity;
import com.binh.todo_api.repository.TodoJpaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.jayway.jsonpath.JsonPath;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TodoListIT extends  ITBase{
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TodoJpaRepository repository;

    @BeforeEach
    void cleanUp(){
        repository.deleteAll();
    }

    private TodoEntity create(String title, int priority ) throws Exception{
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/todos")
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
    void get_list_paging()throws Exception{
        create("A", 1);
        create("B", 2);
        create("C", 3);

        mvc.perform(get("/api/todos").param("page", "0").param("size", "0"))
                .andExpect(status().isOk());
    }
}
