package com.example.opensearchassistant.config;

import com.example.opensearchassistant.service.AdminUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseAdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(AdminUserService adminUserService) {
        return args -> adminUserService.createDefaultAdmin();
    }
}
