package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.BundleEntity;
import org.silentsoft.solarguard.entity.BundleId;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleRepository extends JpaRepository<BundleEntity, BundleId> {

    @Query("select bundle from bundles bundle where bundle.id._package.id = :packageId")
    List<BundleEntity> findAllByPackageId(long packageId);

    @Query("select bundle from bundles bundle where bundle.id.product.id = :productId")
    List<BundleEntity> findAllByProductId(long productId);

    @Modifying
    @Query("delete from bundles where id._package = :_package")
    void deleteAllByPackage(PackageEntity _package);

}
