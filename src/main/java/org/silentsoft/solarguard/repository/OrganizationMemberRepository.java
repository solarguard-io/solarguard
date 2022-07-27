package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.OrganizationMemberEntity;
import org.silentsoft.solarguard.entity.OrganizationMemberId;
import org.silentsoft.solarguard.entity.OrganizationMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMemberEntity, OrganizationMemberId> {

    List<OrganizationMemberEntity> findAllById_OrganizationId(long organizationId);

    boolean existsById_OrganizationIdAndId_UserId(long organizationId, long userId);

    boolean existsById_OrganizationIdAndId_UserIdAndRole(long organizationId, long userId, OrganizationMemberRole role);

}
