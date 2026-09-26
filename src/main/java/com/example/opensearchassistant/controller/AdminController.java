package com.example.opensearchassistant.controller;

import com.example.opensearchassistant.model.AdminLoginRequest;
import com.example.opensearchassistant.model.AdminLoginResponse;
import com.example.opensearchassistant.model.AdminUser;
import com.example.opensearchassistant.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(new AdminLoginResponse("error", "Username and password are required."));
        }

        Optional<AdminUser> adminUser = adminUserService.findByUsername(request.getUsername());
        if (adminUser.isPresent() && adminUser.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok(new AdminLoginResponse("success", "Admin login successful."));
        }

        return ResponseEntity.status(401).body(new AdminLoginResponse("error", "Invalid admin credentials."));
    }

    @PostMapping("/admin/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, Principal principal) {
        if (payload == null || payload.get("currentPassword") == null || payload.get("newPassword") == null) {
            return ResponseEntity.badRequest().body(new AdminLoginResponse("error", "Current password and new password are required."));
        }

        String username = principal != null ? principal.getName() : payload.get("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(new AdminLoginResponse("error", "Admin username is required."));
        }

        try {
            adminUserService.updatePassword(username, payload.get("currentPassword"), payload.get("newPassword"));
            return ResponseEntity.ok(new AdminLoginResponse("success", "Password updated successfully."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new AdminLoginResponse("error", ex.getMessage()));
        }
    }
}
