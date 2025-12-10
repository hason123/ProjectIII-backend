package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.constant.RoleType;
import com.example.projectiii.dto.request.RoleRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.RoleResponse;
import com.example.projectiii.entity.Permission;
import com.example.projectiii.entity.Role;
import com.example.projectiii.exception.BusinessException;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.repository.PermissionRepository;
import com.example.projectiii.repository.RoleRepository;
import com.example.projectiii.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MessageConfig messageConfig;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, MessageConfig messageConfig) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public RoleResponse updateRole(RoleRequest request, Long roleId) {
        log.info("Update role with id {}", roleId);
        Role role = roleRepository.findById(roleId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
        });
        role.setRoleDesc(request.getDescription());
        if(request.getPermissionIds() != null) {
            List<Permission> permissions = request.getPermissionIds().stream().map(id -> permissionRepository.findById(id).orElseThrow(()
                    ->{ log.error(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND), id);
                return new ResourceNotFoundException(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
            })).toList();
            log.info("Adding or removing permissions of a role");
            role.setPermissions(permissions);
        }
        roleRepository.save(role);
        log.info("Role with id {} has been updated", roleId);
        return convertRoleToDTO(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
                    return new BusinessException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
                });
        role.getPermissions().forEach(permission -> permission.getRoles().remove(role));
        role.getUsers().forEach(user -> user.setRole(roleRepository.findByRoleName(RoleType.USER)));
        roleRepository.deleteById(roleId);
        log.info("Role with id {} has been deleted", roleId);
    }


    @Override
    public PageResponse<RoleResponse> getPageRole(Pageable pageable) {
       log.info("Get roles with page {}", pageable);
       Page<Role> roles = roleRepository.findAll(pageable);
       Page<RoleResponse> rolePage = roles.map(this::convertRoleToDTO);
       return new PageResponse<>(rolePage.getNumber() + 1,
               rolePage.getNumberOfElements(),
               rolePage.getTotalPages(),
               rolePage.getContent());
    }

    @Override
    public RoleResponse getRole(Long roleId) {
        log.info("Get role with id {}", roleId);
        Role role  = roleRepository.findById(roleId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
        });
        return convertRoleToDTO(role);
    }

    public RoleResponse convertRoleToDTO(Role role) {
        RoleResponse response = new RoleResponse();
        response.setRoleName(role.getRoleName().toString());
        response.setRoleId(role.getRoleID());
        response.setDescription(role.getRoleDesc());
        List<RoleResponse.PermissionDTO> permissions = role.getPermissions().stream().map(permission ->
                new RoleResponse.PermissionDTO(permission.getId(), permission.getName()))
                .toList();
        response.setPermission(permissions);
        return response;
    }
}
