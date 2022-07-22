package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.DeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, DeviceId> {

    long countAllById_LicenseId(long licenseId);

}
