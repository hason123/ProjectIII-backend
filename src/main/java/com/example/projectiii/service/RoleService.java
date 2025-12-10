package com.example.projectiii.service;

import com.example.projectiii.dto.request.RoleRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.RoleResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleResponse updateRole(RoleRequest roleRequestDTO, Long roleId);

    void deleteRole(Long roleId);

    PageResponse<RoleResponse> getPageRole(Pageable pageable);

    RoleResponse getRole(Long roleId);
}
