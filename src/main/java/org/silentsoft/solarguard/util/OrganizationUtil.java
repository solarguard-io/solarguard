package org.silentsoft.solarguard.util;

import org.silentsoft.solarguard.entity.OrganizationMemberRole;
import org.silentsoft.solarguard.exception.OrganizationNotFoundException;
import org.silentsoft.solarguard.repository.OrganizationMemberRepository;
import org.silentsoft.solarguard.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class OrganizationUtil {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    public void checkMemberAuthority(long organizationId) {
        checkExists(organizationId);
        if (isNotMember(organizationId) && UserUtil.isNotAdmin()) {
            throw new AccessDeniedException(String.format("The user '%d' is not a member of the organization '%d'.", UserUtil.getId(), organizationId));
        }
    }

    public void checkStaffAuthority(long organizationId) {
        checkExists(organizationId);
        if (isNotStaff(organizationId) && UserUtil.isNotAdmin()) {
            throw new AccessDeniedException(String.format("The user '%d' has not STAFF role in organization '%d'.", UserUtil.getId(), organizationId));
        }
    }

    private void checkExists(long organizationId) {
        if (notExists(organizationId)) {
            throw new OrganizationNotFoundException(String.format("The organization '%d' does not exist.", organizationId));
        }
    }

    private boolean notExists(long organizationId) {
        return !exists(organizationId);
    }

    private boolean exists(long organizationId) {
        return organizationRepository.existsById(organizationId);
    }

    private boolean isNotMember(long organizationId) {
        return !isMember(organizationId);
    }

    private boolean isMember(long organizationId) {
        return organizationMemberRepository.existsById_OrganizationIdAndId_UserId(organizationId, UserUtil.getId());
    }

    private boolean isNotStaff(long organizationId) {
        return !isStaff(organizationId);
    }

    private boolean isStaff(long organizationId) {
        return organizationMemberRepository.existsById_OrganizationIdAndId_UserIdAndRole(organizationId, UserUtil.getId(), OrganizationMemberRole.STAFF);
    }

}
