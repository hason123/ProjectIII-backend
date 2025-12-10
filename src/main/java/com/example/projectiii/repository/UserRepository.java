package com.example.projectiii.repository;

import com.example.projectiii.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);

    User findByUserNameAndRefreshToken(String userName, String refreshToken);

    boolean existsByUserName(String userName);

}
