package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.exception.ProductNotFoundException;
import org.silentsoft.solarguard.vo.ProductPatchVO;
import org.silentsoft.solarguard.vo.ProductPostVO;
import org.silentsoft.solarguard.vo.ProductTokenPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class ProductServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProductService productService;

    @Test
    @WithBrowser("admin")
    public void dataIntegrityTest() {
        long productId = organizationService.addProduct(100, ProductPostVO.builder().name(" ProductServiceTest.dataIntegrityTest.product ").build()).getId();

        ProductEntity product = productService.getProduct(productId);
        Assertions.assertEquals("ProductServiceTest.dataIntegrityTest.product", product.getName());

        product = productService.patchProduct(productId, ProductPatchVO.builder().name(" ProductServiceTest.dataIntegrityTest.product.2 ").build());
        Assertions.assertEquals("ProductServiceTest.dataIntegrityTest.product.2", product.getName());

        productService.createToken(productId, ProductTokenPostVO.builder().note(" token1 ").build());
        productService.createToken(productId, ProductTokenPostVO.builder().note(" token2 ").build());
        List<ProductTokenEntity> tokens = productService.getTokens(productId);
        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("token1", tokens.get(0).getNote());
        Assertions.assertEquals("token2", tokens.get(1).getNote());

        productService.deleteProduct(productId);

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(productId);
        });
        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getTokens(productId);
        });
    }

}
