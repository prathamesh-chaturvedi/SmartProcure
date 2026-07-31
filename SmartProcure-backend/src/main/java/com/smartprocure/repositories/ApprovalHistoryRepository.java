package com.smartprocure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartprocure.entities.ApprovalHistory;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

}
