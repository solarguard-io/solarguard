package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Long> {

    List<PackageEntity> findAllByOrganizationId(long organizationId);

}
