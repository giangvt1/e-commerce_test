package com.retailonboardpro.repository;

import com.retailonboardpro.entity.Request;
import com.retailonboardpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByCreatorOrderByCreatedAtDesc(User creator);
    List<Request> findByStatusOrderByCreatedAtDesc(String status);
    List<Request> findAllByOrderByCreatedAtDesc();
} 