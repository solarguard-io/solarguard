package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.entity.*;
import org.silentsoft.solarguard.exception.OrganizationNotFoundException;
import org.silentsoft.solarguard.exception.PackageNotFoundException;
import org.silentsoft.solarguard.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class OrganizationServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private ProductService productService;

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

        List<BundleEntity> bundles = packageService.getBundles(packageEntity.getId());
        Assertions.assertEquals(2, bundles.size());
        Assertions.assertEquals(200L, bundles.get(0).getId().getProduct().getId());
        Assertions.assertEquals(201L, bundles.get(1).getId().getProduct().getId());
    }

    @Test
    @WithBrowser("admin")
    public void dataIntegrityTest() {
        long organizationId = organizationService.createOrganization(OrganizationPostVO.builder().name(" OrganizationServiceTest.dataIntegrityTest.1 ").build()).getId();
        Assertions.assertDoesNotThrow(() -> {
            OrganizationEntity organization = organizationService.getOrganization(organizationId);
            Assertions.assertEquals("OrganizationServiceTest.dataIntegrityTest.1", organization.getName());
        });

        ProductEntity product1 = organizationService.addProduct(organizationId, ProductPostVO.builder().name(" Product 1 ").build());
        ProductEntity product2 = organizationService.addProduct(organizationId, ProductPostVO.builder().name(" Product 2 ").build());
        List<ProductEntity> products = organizationService.getProducts(organizationId);
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals("Product 1", products.get(0).getName());
        Assertions.assertEquals("Product 2", products.get(1).getName());

        ProductTokenEntity token1_1 = productService.createToken(product1.getId(), ProductTokenPostVO.builder().note(" Token 1-1 ").build());
        ProductTokenEntity token2_1 = productService.createToken(product2.getId(), ProductTokenPostVO.builder().note(" Token 2-1 ").build());
        ProductTokenEntity token2_2 = productService.createToken(product2.getId(), ProductTokenPostVO.builder().note(" Token 2-2 ").build());
        List<ProductTokenEntity> tokens1 = productService.getTokens(product1.getId());
        Assertions.assertEquals(1, tokens1.size());
        Assertions.assertEquals("Token 1-1", tokens1.get(0).getNote());
        List<ProductTokenEntity> tokens2 = productService.getTokens(product2.getId());
        Assertions.assertEquals(2, tokens2.size());
        Assertions.assertEquals("Token 2-1", tokens2.get(0).getNote());
        Assertions.assertEquals("Token 2-2", tokens2.get(1).getNote());

        PackageEntity package1 = organizationService.addPackage(organizationId, PackagePostVO.builder().name(" Package 1 ").productIds(Arrays.asList(product1.getId())).build());
        PackageEntity package2 = organizationService.addPackage(organizationId, PackagePostVO.builder().name(" Package 2 ").productIds(Arrays.asList(product2.getId())).build());
        PackageEntity package3 = organizationService.addPackage(organizationId, PackagePostVO.builder().name(" Package 3 ").productIds(Arrays.asList(product1.getId(), product2.getId())).build());
        List<PackageEntity> packages = organizationService.getPackages(organizationId);
        Assertions.assertEquals(3, packages.size());
        Assertions.assertEquals("Package 1", packages.get(0).getName());
        Assertions.assertEquals("Package 2", packages.get(1).getName());
        Assertions.assertEquals("Package 3", packages.get(2).getName());

        LicenseEntity license1_1 = packageService.issueLicense(package1.getId(), LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).note(" License 1-1 ").build());
        LicenseEntity license2_1 = packageService.issueLicense(package2.getId(), LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).note(" License 2-1 ").build());
        LicenseEntity license2_2 = packageService.issueLicense(package2.getId(), LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).note(" License 2-2 ").build());
        LicenseEntity license3_1 = packageService.issueLicense(package3.getId(), LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now().plusDays(1)).note(" License 3-1 ").build());
        LicenseEntity license3_2 = packageService.issueLicense(package3.getId(), LicensePostVO.builder().licenseType(LicenseType.SUBSCRIPTION).expiredAt(LocalDate.now().plusDays(1)).note(" License 3-2 ").deviceLimited(true).deviceLimit(2L).build());
        List<LicenseEntity> licenses1 = packageService.getLicenses(package1.getId());
        List<LicenseEntity> licenses2 = packageService.getLicenses(package2.getId());
        List<LicenseEntity> licenses3 = packageService.getLicenses(package3.getId());
        Assertions.assertEquals(1, licenses1.size());
        Assertions.assertEquals("License 1-1", licenses1.get(0).getNote());
        Assertions.assertEquals(2, licenses2.size());
        Assertions.assertEquals("License 2-1", licenses2.get(0).getNote());
        Assertions.assertEquals("License 2-2", licenses2.get(1).getNote());
        Assertions.assertEquals(2, licenses3.size());
        Assertions.assertEquals("License 3-1", licenses3.get(0).getNote());
        Assertions.assertEquals("License 3-2", licenses3.get(1).getNote());

        organizationService.deleteOrganization(organizationId);
        Assertions.assertThrows(OrganizationNotFoundException.class, () -> {
            organizationService.getOrganization(organizationId);
        });
        Assertions.assertThrows(OrganizationNotFoundException.class, () -> {
            organizationService.getPackages(organizationId);
        });
        Assertions.assertThrows(OrganizationNotFoundException.class, () -> {
            organizationService.getProducts(organizationId);
        });
        Assertions.assertThrows(PackageNotFoundException.class, () -> {
            packageService.getLicenses(package1.getId());
        });
        Assertions.assertThrows(PackageNotFoundException.class, () -> {
            packageService.getBundles(package1.getId());
        });
    }

}
