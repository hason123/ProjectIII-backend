package com.example.projectiii.repository;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    User findByUserName(String userName);

    User findByUserNameAndRefreshToken(String userName, String refreshToken);

    User findByGmail(String gmail);

   List<User> findByRole_RoleName(RoleType roleName);

}
