package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.constant.RoleType;
import com.example.projectiii.dto.request.PermissionRequest;
import com.example.projectiii.dto.request.search.SearchPermissionRequest;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.PermissionResponse;
import com.example.projectiii.entity.Permission;
import com.example.projectiii.entity.Role;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.repository.PermissionRepository;
import com.example.projectiii.repository.RoleRepository;
import com.example.projectiii.service.PermissionService;
import com.example.projectiii.specification.PermissionSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final MessageConfig messageConfig;

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository, MessageConfig messageConfig) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        log.info("create a new permission");
        Permission permission = new Permission();
        if(permissionRepository.existsByName(request.getName())) {
            throw new DataIntegrityViolationException(MessageError.PERMISSION_NAME_UNIQUE);
        }
        else permission.setName(request.getName());
        permission.setApiPath(request.getApiPath());
        permission.setMethod(request.getMethod());
        permission.setDescription(request.getDescription());
        if (request.getRoleIds() != null) {
            List<Role> roles = request.getRoleIds().stream().map(id ->
                    roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, id)))
            ).toList();
            permission.setRoles(roles);
        }
        log.info("successfully create new permission");
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        log.info("update permission with id {}", id);
        Permission permission =  permissionRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND), id);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
        });
        if (request.getName() != null) {
            if(permissionRepository.existsByName(request.getName())) {
               permission.setName(permission.getName());
            }
            permission.setName(request.getName());
        } else permission.setName(permission.getName());
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
        if (request.getApiPath() != null) {
            permission.setApiPath(request.getApiPath());
        } else permission.setApiPath(permission.getApiPath());
        if (request.getMethod() != null) {
            permission.setMethod(request.getMethod());
        } else permission.setMethod(permission.getMethod());
        if (request.getRoleIds() != null) {
            List<Role> roles = request.getRoleIds().stream().map(roleId ->
                    roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId)))
            ).toList();
            permission.setRoles(roles);
        }
        log.info("successfully update permission with id {}", id);
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        log.info("delete permission with id {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    log.info(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
                    return new ResourceNotFoundException(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
                });
        permission.getRoles().forEach(role -> {
            role.getPermissions().remove(permission);
            roleRepository.save(role);
        });
        permissionRepository.deleteById(id);
        log.info("Permission with id {} has been deleted", id);
    }

    @Override
    public PageResponse<PermissionResponse> getPagePermission(Pageable pageable) {
        log.info("Get permission's page");
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        Page<PermissionResponse> permissionPage = permissions.map(this::convertPermissionToDTO);
        log.info("Return permission's page");
        return new PageResponse<>(permissionPage.getNumber() + 1,
                permissionPage.getNumberOfElements(),
                permissionPage.getTotalPages(),
                permissionPage.getContent());
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        log.info("get permission with id {}", id);
        Permission permission =  permissionRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND), id);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
        });
        return convertPermissionToDTO(permission);
    }

    @Override
    public PageResponse<PermissionResponse> searchPermission(Pageable pageable, SearchPermissionRequest request){
        log.info("Searching permissions from database");
        Specification<Permission> spec = ((root, query, cb) -> cb.conjunction());

        if (StringUtils.hasText(request.getName())) {
            spec = spec.and(PermissionSpecification.likeName(request.getName()));
        }
        if (StringUtils.hasText(request.getApiPath())) {
            spec = spec.and(PermissionSpecification.likeApiPath(request.getApiPath()));
        }
        if (StringUtils.hasText(request.getMethod())) {
            spec = spec.and(PermissionSpecification.hasMethod(request.getMethod()));
        }
        if (StringUtils.hasText(request.getDescription())) {
            spec = spec.and(PermissionSpecification.likeDescription(request.getDescription()));
        }
        if (StringUtils.hasText(request.getRoleName())) {
            try {
                RoleType roleType = RoleType.valueOf(request.getRoleName().toUpperCase());
                spec = spec.and(PermissionSpecification.hasRole(roleType));
            } catch (IllegalArgumentException ignored) {

            }
        }
        Page<Permission> permissions = permissionRepository.findAll(spec, pageable);
        Page<PermissionResponse> permissionPage = permissions.map(this::convertPermissionToDTO);
        log.info("Retrieved all permissions from database");
        return new PageResponse<>(permissionPage.getNumber(), permissionPage.getNumberOfElements(),
                permissionPage.getTotalPages(), permissionPage.getContent());
    }

    public PermissionResponse convertPermissionToDTO(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setApiPath(permission.getApiPath());
        response.setMethod(permission.getMethod());
        response.setDescription(permission.getDescription());
        response.setRoleName(permission.getRoles().stream().map(role -> role.getRoleName().toString()).toList());
        return response;
    }
}
