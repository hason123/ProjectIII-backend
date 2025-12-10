package com.example.projectiii.repository;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(RoleType roleName);

    boolean existsByRoleName(RoleType roleName);
}
