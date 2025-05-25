package com.retailonboardpro.controller;

import com.retailonboardpro.constant.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReferenceDataController {

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(AppConstants.REQUEST_CATEGORIES);
    }
    
    @GetMapping("/statuses")
    public ResponseEntity<Map<String, Object>> getStatuses() {
        Map<String, Object> statusLabels = new HashMap<>();
        
        statusLabels.put("Pending_Manager", Map.of("text", "Chờ xem xét", "color", "warning"));
        statusLabels.put("Rejected_Manager", Map.of("text", "Cần sửa lại", "color", "destructive"));
        statusLabels.put("Pending_Director", Map.of("text", "Chờ Giám đốc", "color", "warning"));
        statusLabels.put("Rejected_Director", Map.of("text", "Bị từ chối bởi Giám đốc", "color", "destructive"));
        statusLabels.put("Pending_CEO", Map.of("text", "Chờ CEO", "color", "warning"));
        statusLabels.put("Rejected_CEO", Map.of("text", "Bị từ chối bởi CEO", "color", "destructive"));
        statusLabels.put("Approved", Map.of("text", "Đã phê duyệt", "color", "success"));
        
        return ResponseEntity.ok(statusLabels);
    }
    
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(AppConstants.ROLES);
    }
} 