package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.entity.LicenseType;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.exception.PackageNotFoundException;
import org.silentsoft.solarguard.vo.LicensePostVO;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.silentsoft.solarguard.vo.PackagePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@ActiveProfiles("dev")
public class PackageServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PackageService packageService;

    @Test
    @WithUserDetails("admin")
    public void transactionTest() {
        long packageId = organizationService.addPackage(100, PackagePostVO.builder().name("PackageServiceTest.transactionTest.package.1").productIds(Arrays.asList(200L)).build()).getId();

        PackageEntity entity = packageService.getPackage(packageId);
        Assertions.assertEquals("PackageServiceTest.transactionTest.package.1", entity.getName());
        Assertions.assertEquals(1, packageService.getBundles(packageId).size());

        // Transaction rollback: Duplicate products are not allowed.
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.patchPackage(packageId, PackagePatchVO.builder().name("PackageServiceTest.transactionTest.package.2").productIds(Arrays.asList(200L, 200L, 201L)).build());
        });
        entity = packageService.getPackage(packageId);
        Assertions.assertEquals("PackageServiceTest.transactionTest.package.1", entity.getName());
        Assertions.assertEquals(1, packageService.getBundles(packageId).size());
    }

    @Test
    @WithUserDetails("admin")
    public void dataIntegrityTest() {
        long packageId = organizationService.addPackage(100, PackagePostVO.builder().name(" PackageServiceTest.dataIntegrityTest.package.1 ").productIds(Arrays.asList(200L)).build()).getId();

        PackageEntity packageEntity = packageService.getPackage(packageId);
        Assertions.assertEquals("PackageServiceTest.dataIntegrityTest.package.1", packageEntity.getName());
        Assertions.assertEquals(1, packageService.getBundles(packageId).size());

        packageService.patchPackage(packageId, PackagePatchVO.builder().productIds(Collections.emptyList()).build());
        Assertions.assertEquals(0, packageService.getBundles(packageId).size());

        packageEntity = packageService.patchPackage(packageId, PackagePatchVO.builder().name(" PackageServiceTest.dataIntegrityTest.package.2 ").productIds(Arrays.asList(200L, 201L)).build());
        Assertions.assertEquals("PackageServiceTest.dataIntegrityTest.package.2", packageEntity.getName());
        Assertions.assertEquals(2, packageService.getBundles(packageId).size());

        packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build());
        packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build());
        Assertions.assertEquals(2, packageService.getLicenses(packageId).size());

        packageService.deletePackage(packageId);

        Assertions.assertThrows(PackageNotFoundException.class, () -> {
            packageService.getPackage(packageId);
        });
        Assertions.assertThrows(PackageNotFoundException.class, () -> {
            packageService.getBundles(packageId);
        });
        Assertions.assertThrows(PackageNotFoundException.class, () -> {
            packageService.getLicenses(packageId);
        });
    }

    @Test
    @WithUserDetails("admin")
    public void issueLicenseTest() {
        long packageId = organizationService.addPackage(100, PackagePostVO.builder().name("PackageServiceTest.issueLicenseTest.package.1").productIds(Arrays.asList(200L)).build()).getId();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now().minusDays(1)).build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now()).deviceLimited(true).build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now()).deviceLimited(true).deviceLimit(-1L).build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now()).deviceLimited(true).deviceLimit(0L).build());
        });

        Assertions.assertEquals(0, packageService.getLicenses(packageId).size());

        LicenseEntity licenseEntity = packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now()).deviceLimited(true).deviceLimit(1L).note(" NOTE ").build());

        Assertions.assertEquals(1, packageService.getLicenses(packageId).size());
        Assertions.assertEquals(packageId, licenseEntity.getPackage().getId());
        Assertions.assertNotNull(licenseEntity.getKey());
        Assertions.assertEquals(LicenseType.SUBSCRIPTION, licenseEntity.getType());
        Assertions.assertEquals(LocalDate.now(), licenseEntity.getExpiredAt().toLocalDate());
        Assertions.assertTrue(licenseEntity.getIsDeviceLimited());
        Assertions.assertEquals(1L, licenseEntity.getDeviceLimit());
        Assertions.assertEquals("NOTE", licenseEntity.getNote());

        packageService.issueLicense(packageId, LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build());
        Assertions.assertEquals(2, packageService.getLicenses(packageId).size());
    }

}
