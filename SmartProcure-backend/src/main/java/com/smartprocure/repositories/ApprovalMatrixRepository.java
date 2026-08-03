package com.smartprocure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartprocure.entities.ApprovalMatrix;

public interface ApprovalMatrixRepository extends JpaRepository<ApprovalMatrix, Long>{

	List<ApprovalMatrix> findByCompanyCompanyId(Long companyId);
	
	Optional<ApprovalMatrix>
	findByCompanyCompanyIdAndApprovalLevel(
	        Long companyId,
	        Integer approvalLevel);
	
}
