package com.todo.todo_api;

import com.todo.todo_api.repository.TaskRepository;
import com.todo.todo_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAndTaskIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void protectedEndpointReturnsJsonUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void userCannotReadOrDeleteAnotherUsersTask() throws Exception {
        register("owner1", "owner1@example.com");
        register("other1", "other1@example.com");

        String ownerToken = login("owner1");
        String otherToken = login("other1");
        long taskId = createTask(ownerToken, "Private task", "HIGH");

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Private task"));
    }

    @Test
    void searchFiltersTasksAndKeepsUserIsolation() throws Exception {
        register("owner1", "owner1@example.com");
        register("other1", "other1@example.com");

        String ownerToken = login("owner1");
        String otherToken = login("other1");

        createTask(ownerToken, "Learn Spring Security", "HIGH");
        createTask(ownerToken, "Buy groceries", "LOW");
        createTask(otherToken, "Learn Spring Security privately", "HIGH");

        mockMvc.perform(get("/tasks/search")
                        .header("Authorization", bearer(ownerToken))
                        .param("query", "Spring")
                        .param("priority", "HIGH")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Learn Spring Security"))
                .andExpect(jsonPath("$.content[0].username").value("owner1"));
    }

    private void register(String username, String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "email": "%s",
                                  "password": "password123"
                                }
                                """.formatted(username, email)))
                .andExpect(status().isCreated());
    }

    private String login(String username) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "password123"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readTree(response).get("token").stringValue();
    }

    private long createTask(String token, String title, String priority) throws Exception {
        String response = mockMvc.perform(post("/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "description": "Integration test task",
                                  "priority": "%s"
                                }
                                """.formatted(title, priority)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readTree(response).get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
