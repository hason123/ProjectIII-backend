package com.example.projectiii.controller;

import com.example.projectiii.dto.request.PermissionRequest;
import com.example.projectiii.dto.request.search.SearchPermissionRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.PermissionResponse;
import com.example.projectiii.service.PermissionService;
//import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        PermissionResponse permissionCreated = permissionService.createPermission(request);
        return ResponseEntity.ok(permissionCreated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable long id, @RequestBody PermissionRequest request) {
        PermissionResponse permissionUpdated = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(permissionUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable long id) {
        PermissionResponse permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable long id) {
        permissionService.deletePermission(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<PageResponse<PermissionResponse>> getPagePermission(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponse<PermissionResponse> permissionPage = permissionService.getPagePermission(pageable);
        return ResponseEntity.ok(permissionPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions/search")
    public ResponseEntity<PageResponse<PermissionResponse>> searchPermission(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            SearchPermissionRequest request
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<PermissionResponse> permissions = permissionService.searchPermission(pageable, request);
        return ResponseEntity.ok(permissions);
    }

}
