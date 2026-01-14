package com.example.projectiii.repository;

import com.example.projectiii.constant.OtpType;
import com.example.projectiii.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Integer> {
    Optional<Otp> findByCodeAndUser_IdAndTypeAndVerifiedIsFalseAndExpiresAtAfter(
            String code,
            Integer id,
            OtpType type,
            LocalDateTime now
    );

    List<Otp> findByUser_IdAndVerifiedIsFalseAndExpiresAtBefore(
            Integer id,
            LocalDateTime now
    );

    Optional<Otp> findTopByUser_IdAndTypeAndVerifiedFalseOrderByCreatedAtDesc(
            Integer id,
            OtpType type
    );

}
