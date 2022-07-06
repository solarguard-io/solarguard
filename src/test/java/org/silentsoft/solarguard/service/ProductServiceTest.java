package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.vo.ProductPatchVO;
import org.silentsoft.solarguard.vo.ProductPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class ProductServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProductService productService;

    @Test
    @WithUserDetails("admin")
    public void dataIntegrityTest() {
        long productId = organizationService.addProduct(100, ProductPostVO.builder().name("ProductServiceTest.dataIntegrityTest.product").build()).getId();

        ProductEntity product = productService.getProduct(productId);
        Assertions.assertEquals("ProductServiceTest.dataIntegrityTest.product", product.getName());

        product = productService.patchProduct(productId, ProductPatchVO.builder().name("ProductServiceTest.dataIntegrityTest.product.2").build());
        Assertions.assertEquals("ProductServiceTest.dataIntegrityTest.product.2", product.getName());
    }

}
