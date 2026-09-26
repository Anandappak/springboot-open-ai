package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.AdminUser;
import com.example.opensearchassistant.repository.AdminUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;

    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public Optional<AdminUser> findByUsername(String username) {
        Optional<AdminUser> found = adminUserRepository.findByUsername(username);
        if (found.isEmpty() && "admin".equals(username)) {
            return Optional.of(createDefaultAdmin());
        }
        return found;
    }

    public AdminUser createDefaultAdmin() {
        return adminUserRepository.findByUsername("admin")
                .orElseGet(() -> adminUserRepository.save(new AdminUser("admin", "temple123", "ADMIN")));
    }

    public AdminUser updatePassword(String username, String currentPassword, String newPassword) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseGet(() -> {
                    if ("admin".equals(username)) {
                        return createDefaultAdmin();
                    }
                    throw new IllegalArgumentException("Admin user not found");
                });

        if (!adminUser.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 4) {
            throw new IllegalArgumentException("New password must be at least 4 characters long");
        }

        adminUser.setPassword(newPassword);
        return adminUserRepository.save(adminUser);
    }
}
