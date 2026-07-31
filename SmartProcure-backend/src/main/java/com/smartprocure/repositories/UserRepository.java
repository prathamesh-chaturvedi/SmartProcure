package com.smartprocure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartprocure.entities.Designation;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;


public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	
	Optional<User> findByUserId(Long userId);

	boolean existsByEmail(String email);

	List<User> findByCompanyCompanyId(Long companyId);
	
	boolean existsByUserRole(UserRole role);

	@Query("""
		    SELECT u FROM User u
		    WHERE (:name IS NULL OR
		         LOWER(CONCAT(u.firstName, ' ', u.lastName))
		         LIKE LOWER(CONCAT('%', :name, '%')))
		    AND (:companyId IS NULL OR u.company.companyId = :companyId)
		    AND (:userRole IS NULL OR u.userRole = :userRole)
		    AND (:designation IS NULL OR u.designation = :designation)
		    AND (:isActive IS NULL OR u.isActive = :isActive)
		    """)
		Page<User> getUsers(
		        @Param("name") String name,
		        @Param("companyId") Long companyId,
		        @Param("userRole") UserRole userRole,
		        @Param("designation") Designation designation,
		        @Param("isActive") Boolean isActive,
		        Pageable pageable);
}
