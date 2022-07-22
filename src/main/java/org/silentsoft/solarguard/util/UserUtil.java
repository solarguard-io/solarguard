package org.silentsoft.solarguard.util;

import org.silentsoft.solarguard.core.userdetails.UserDetails;
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.entity.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static long getId() {
        return getEntity().getId();
    }

    public static UserEntity getEntity() {
        UserEntity userEntity = getDetails().getUserEntity();
        userEntity.setPassword(null);
        return userEntity;
    }

    private static UserDetails getDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (UserDetails) securityContext.getAuthentication().getPrincipal();
    }

    public static boolean isNotAdmin() {
        return !isAdmin();
    }

    public static boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(UserRole.ADMIN.name()));
    }

    public static void checkIdentity(long userId) {
        UserEntity user = getEntity();

        long currentUserId = user.getId();
        if (currentUserId != userId && isNotAdmin()) {
            throw new AccessDeniedException(String.format("Must be the same user or admin to perform this action. (current: %d, target: %d)", currentUserId, userId));
        }
    }

    public static void checkAdminAuthority() {
        if (isNotAdmin()) {
            throw new AccessDeniedException(String.format("The user '%d' is not allowed to perform this action because does not have admin authority.", getId()));
        }
    }

}
