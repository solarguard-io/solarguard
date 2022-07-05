package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.entity.BundleEntity;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.repository.BundleRepository;
import org.silentsoft.solarguard.vo.PackagePostVO;
import org.silentsoft.solarguard.vo.ProductPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class OrganizationServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private BundleRepository bundleRepository;

    @Test
    @WithUserDetails("admin")
    public void addProductWithStaffAuthority() {
        ProductEntity productEntity = organizationService.addProduct(100, ProductPostVO.builder().name("Awesome Product").build());
        Assertions.assertEquals("Awesome Product", productEntity.getName());
    }

    @Test
    @WithUserDetails("admin")
    public void addPackageWithStaffAuthorityTest() {
        PackageEntity packageEntity = organizationService.addPackage(100, PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 201L)).build());
        Assertions.assertEquals("Awesome Package", packageEntity.getName());

        List<BundleEntity> bundles = bundleRepository.findAllById_PackageId(packageEntity.getId());
        Assertions.assertEquals(2, bundles.size());
        Assertions.assertEquals(200L, bundles.get(0).getId().getProductId());
        Assertions.assertEquals(201L, bundles.get(1).getId().getProductId());
    }

}
