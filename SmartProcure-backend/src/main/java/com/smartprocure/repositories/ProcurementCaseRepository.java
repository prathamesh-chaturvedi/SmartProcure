package com.smartprocure.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.ProcurementStatus;

public interface ProcurementCaseRepository extends JpaRepository<ProcurementCase, Long> {

    @Query("""
        SELECT pc
        FROM ProcurementCase pc
        WHERE pc.isDeleted = false
        AND (:companyId IS NULL OR pc.createdBy.company.companyId = :companyId)
        AND (:userId IS NULL OR pc.createdBy.userId = :userId)
        AND (:status IS NULL OR pc.status = :status)
        AND (:procurementCode IS NULL OR LOWER(pc.procurementCode) LIKE LOWER(CONCAT('%', :procurementCode, '%')))
        AND (:title IS NULL OR LOWER(pc.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:minAmount IS NULL OR pc.packageAmount >= :minAmount)
        AND (:maxAmount IS NULL OR pc.packageAmount <= :maxAmount)
        AND (:fromDate IS NULL OR pc.createdAt >= :fromDate)
        AND (:toDate IS NULL OR pc.createdAt <= :toDate)
        """)
    Page<ProcurementCase> searchProcurementCases(
            @Param("companyId") Long companyId,
            @Param("userId") Long userId,
            @Param("status") ProcurementStatus status,
            @Param("procurementCode") String procurementCode,
            @Param("title") String title,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}