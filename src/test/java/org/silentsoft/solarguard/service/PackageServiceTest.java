package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.silentsoft.solarguard.vo.PackagePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

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
        long packageId = organizationService.addPackage(100, PackagePostVO.builder().name("PackageServiceTest.dataIntegrityTest.package.1").productIds(Arrays.asList(200L)).build()).getId();

        PackageEntity entity = packageService.getPackage(packageId);
        Assertions.assertEquals("PackageServiceTest.dataIntegrityTest.package.1", entity.getName());
        Assertions.assertEquals(1, packageService.getBundles(packageId).size());

        entity = packageService.patchPackage(packageId, PackagePatchVO.builder().name("PackageServiceTest.dataIntegrityTest.package.2").productIds(Arrays.asList(200L, 201L)).build());
        Assertions.assertEquals("PackageServiceTest.dataIntegrityTest.package.2", entity.getName());
        Assertions.assertEquals(2, packageService.getBundles(packageId).size());

        entity = packageService.patchPackage(packageId, PackagePatchVO.builder().productIds(Collections.emptyList()).build());
        Assertions.assertEquals("PackageServiceTest.dataIntegrityTest.package.2", entity.getName());
        Assertions.assertEquals(0, packageService.getBundles(packageId).size());
    }

}
