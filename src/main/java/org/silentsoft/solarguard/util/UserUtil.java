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

    public static void checkAdminAuthority() {
        if (!isAdmin()) {
            throw new AccessDeniedException(String.format("The user '%d' is not an admin.", UserUtil.getId()));
        }
    }

}
