package com.example.projectiii.service;

import com.example.projectiii.dto.request.PermissionRequest;
import com.example.projectiii.dto.request.search.SearchPermissionRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.PermissionResponse;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);

    PermissionResponse updatePermission(Long id, PermissionRequest request);

    void deletePermission(Long id);

    PageResponse<PermissionResponse> getPagePermission(Pageable pageable);

    PermissionResponse getPermissionById(Long id);

    PageResponse<PermissionResponse> searchPermission(Pageable pageable, SearchPermissionRequest request);
}


