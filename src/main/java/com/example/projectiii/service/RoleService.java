package com.example.projectiii.service;

import com.example.projectiii.dto.request.RoleRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.RoleResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleResponse updateRole(RoleRequest roleRequestDTO, Integer roleId);

    void deleteRole(Integer roleId);

    PageResponse<RoleResponse> getPageRole(Pageable pageable);

    RoleResponse getRole(Integer roleId);
}
