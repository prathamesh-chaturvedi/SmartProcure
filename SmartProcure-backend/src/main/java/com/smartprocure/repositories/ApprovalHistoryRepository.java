package com.smartprocure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartprocure.entities.Action;
import com.smartprocure.entities.ApprovalHistory;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

	Optional<ApprovalHistory>
	findFirstByProcurementCaseProcurementCaseIdAndAction(
	        Long csId,
	        Action action);
	
	List<ApprovalHistory> findByApproverUserIdAndAction(
	        Long userId,
	        Action action);
	
}
