package com.smartprocure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;
import com.smartprocure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserService {
	
	private final UserRepository userRepository;
	
	public Long getCurrentUserId() 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Long userId = ((JwtPrincipal) auth.getPrincipal()).getUserId();
		
		return userId;
		
	}
	
	public User getCurrentUser() {
		
		return userRepository.findById(getCurrentUserId())
    			.orElseThrow(()-> new ResourceNotFoundException("Invalid User Id"));
		
	}
	
	public Long getCurrentUserCompanyId() 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Long companyId = ((JwtPrincipal) auth.getPrincipal()).getCompanyId();
		
		return companyId;
		
	}

	public UserRole getCurrentUserRole() {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    String authority = auth.getAuthorities()
	                           .iterator()
	                           .next()
	                           .getAuthority();

	    if ("ROLE_ANONYMOUS".equals(authority)) {
	        throw new AccessDeniedException("Authentication required.");
	    }
	    return UserRole.valueOf(authority);
	}
	
}
