package com.smartprocure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;
import com.smartprocure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessValidator {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public void validateActiveUser() {

        Long currentUserId = currentUserService.getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found."));

        if (!currentUser.isActive()) {
            throw new AccessDeniedException(
                    "Your account has been deactivated. Please contact your administrator.");
        }
    }

    public void validateUserAccess(User user) {

        if (!currentUserService.getCurrentUserRole().equals(UserRole.MASTER_ADMIN)
                && !currentUserService.getCurrentUserCompanyId()
                        .equals(user.getCompany().getCompanyId())) {

            throw new AccessDeniedException(
                    "You are not authorized to access this user.");
        }
    }
    
    public void validateUserAccess() {

        Long currentUserId = currentUserService.getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found."));

        validateUserAccess(currentUser);
    }
}
