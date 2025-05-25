package com.retailonboardpro.repository;

import com.retailonboardpro.entity.ApprovalHistory;
import com.retailonboardpro.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByRequestOrderByActionAtAsc(Request request);
} 