package com.example.projectiii.specification;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.entity.Permission;
import com.example.projectiii.entity.Role;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class PermissionSpecification {
    public static Specification<Permission> likeName(String name) {
        return (root,query, cb)
                -> cb.like(root.get("name"), "%" + name.toLowerCase() + "%");
    }
    public static Specification<Permission> hasMethod(String method) {
        return (root,query,cb) ->
                cb.equal(root.get("method"), method);
    }
    public static Specification<Permission> likeDescription(String description) {
        return (root,query,cb) ->
                cb.like(root.get("description"), "%" + description.toLowerCase() + "%");
    }
    public static Specification<Permission> likeApiPath(String apiPath) {
        return (root,query,cb) ->
                cb.like(root.get("api_path"), "%" + apiPath.toLowerCase() + "%");
    }
    public static Specification<Permission> hasRole(RoleType roleType) {
        return (root, query, cb) -> {
            Join<Permission, Role> roleJoin = root.join("roles", JoinType.LEFT);
            return cb.equal(roleJoin.get("roleName"), roleType);
        };
    }

}
