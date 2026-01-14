package com.example.projectiii.controller;

import com.example.projectiii.dto.request.RoleRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.RoleResponse;
import com.example.projectiii.service.impl.RoleServiceImpl;
//import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class RoleController {

    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Integer id, @RequestBody RoleRequest request) {
        RoleResponse roleUpdated = roleService.updateRole(request, id);
        return ResponseEntity.ok(roleUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Integer id) {
        RoleResponse role = roleService.getRole(id);
        return ResponseEntity.ok(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<PageResponse<RoleResponse>> getPageRole(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponse<RoleResponse> rolePage = roleService.getPageRole(pageable);
        return ResponseEntity.ok(rolePage);
    }


}
