package com.example.opensearchassistant.controller;

import com.example.opensearchassistant.model.AdminUser;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.repository.AdminUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TempleManagementControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @BeforeEach
    void resetDefaultAdmin() {
        adminUserRepository.findByUsername("admin")
                .ifPresent(admin -> {
                    admin.setPassword("temple123");
                    adminUserRepository.save(admin);
                });
    }

    @Test
    void adminLogin_shouldAcceptConfiguredCredentials() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"temple123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void adminPasswordChange_shouldUpdateSavedPassword() throws Exception {
        mockMvc.perform(post("/api/admin/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("admin", "temple123"))
                        .content("{\"currentPassword\":\"temple123\",\"newPassword\":\"newTemple456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"newTemple456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(username = "admin", password = "temple123", roles = "ADMIN")
    void memberCrud_shouldSupportUpdateAndDelete() throws Exception {
        TempleMember member = new TempleMember(null, "Asha", "Caretaker", "0770000001", "Village Road");

        String createdJson = mockMvc.perform(post("/api/temple/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member))
                        .with(httpBasic("admin", "temple123")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TempleMember created = objectMapper.readValue(createdJson, TempleMember.class);

        created.setRole("Senior Caretaker");

        mockMvc.perform(post("/api/temple/members/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created))
                        .with(httpBasic("admin", "temple123")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/temple/members/{id}", created.getId())
                        .with(httpBasic("admin", "temple123")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/temple/members")
                        .with(httpBasic("admin", "temple123")))
                .andExpect(status().isOk());
    }
}
