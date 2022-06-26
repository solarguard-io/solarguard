package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, Long> {

    LicenseEntity findBy_packageInAndKey(List<PackageEntity> packages, String key);

    LicenseEntity findByKey(String key);

}
