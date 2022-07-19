package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, Long> {

    List<LicenseEntity> findAllBy_package(PackageEntity _package);

    List<LicenseEntity> findAllBy_packageIn(List<PackageEntity> packages);

    LicenseEntity findBy_packageInAndKey(List<PackageEntity> packages, String key);

    LicenseEntity findByKey(String key);

    @Modifying
    @Query("delete from licenses where _package.id in :packageIds")
    void deleteAllByPackageIdIn(List<Long> packageIds);

}
