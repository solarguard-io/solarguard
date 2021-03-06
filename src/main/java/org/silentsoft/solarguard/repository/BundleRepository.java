package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.BundleEntity;
import org.silentsoft.solarguard.entity.BundleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleRepository extends JpaRepository<BundleEntity, BundleId> {

    List<BundleEntity> findAllById_PackageId(long packageId);

    List<BundleEntity> findAllById_ProductId(long productId);

    @Modifying
    @Query("delete from bundles where id.packageId = :packageId")
    void deleteAllByPackageId(long packageId);

}
