package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.DeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, DeviceId> {

    long countAllById_LicenseId(long licenseId);

    @Modifying
    @Query("delete from devices where id.licenseId in :licenseIds")
    void deleteAllByLicenseIdIn(List<Long> licenseIds);

}
