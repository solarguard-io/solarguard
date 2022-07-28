package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.DeviceId;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, DeviceId> {

    long countAllById_LicenseId(long licenseId);

    @Query("select device from devices device where device.id.license.id = :licenseId")
    List<DeviceEntity> findAllByLicenseId(long licenseId);

    @Modifying
    @Query("delete from devices where id.license = :license")
    void deleteAllByLicense(LicenseEntity license);

}
