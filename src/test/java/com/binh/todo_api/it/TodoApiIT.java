package com.binh.todo_api.it;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class TodoApiIT extends ITBase {
    @Autowired
    MockMvc mvc;

    @Test
    void createTodo_ok() throws Exception {
        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Learn IT",
                                  "completed": false,
                                  "description": "testcontainers",
                                  "priority": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title").value("Learn IT"));
    }

    @Test
    void createTodo_validatonFail_400() throws Exception {
        mvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                                "title": "   ",
                                                                  "completed": false,
                                                                  "description": "x",
                                                                  "priority": 0
                        }
                        """))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.fieldErrors.title", containsString("blank")));
    }

    private void create(String title, int priority) throws Exception {
        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "completed": false,
                                  "description": "d",
                                  "priority": %d
                                }
                                """.formatted(title, priority)))
                .andExpect(status().isCreated());
    }

    @Test
    void listTodos_paging_ok() throws Exception {
        // create 2
        create("A", 1);
        create("B", 3);

        mvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "priority,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("A"));
    }

    @Test
    void post_ok() throws Exception{
        mvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                         {"title":"A","completed":false,"description":"d","priority":1}
                        """))
                .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", notNullValue()))
                                .andExpect(jsonPath("$.title").value("A"));
    }

    @Test
    void post_fail_400() throws Exception{
        mvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"","completed":false,"description":"d","priority":1}
                """)).andExpect(status().is4xxClientError()).
                andExpect(jsonPath("$.fieldErrors.title", containsString("blank")));
    }

    @Test
    void not_found() throws Exception{
        mvc.perform(get("/api/todos/99999999")).andExpect(status().is4xxClientError());
    }

    @Test
    void get_list_paging() throws Exception{
        create("A", 1);
        create("B", 2);
        mvc.perform(get("/api/todos").param("size", "1")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("A"))
                .andExpect(jsonPath("$[0].completed").value("false"))
                .andExpect(jsonPath("$[0].description").value("d"))
                .andExpect(jsonPath("$[0].priority").value("1"));
    }

    @Test
    void patch_should_update_only_provided_fields() throws Exception{
        create("A", 1);
        mvc.perform(patch("/api/todos/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"completed": true}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));
    }
}