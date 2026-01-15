package com.example.projectiii.repository;

import com.example.projectiii.entity.RenewRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RenewRequestRepository extends JpaRepository<RenewRequest, Long> {



}
