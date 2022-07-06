package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.exception.ProductTokenNotFoundException;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.ProductPostVO;
import org.silentsoft.solarguard.vo.ProductTokenPatchVO;
import org.silentsoft.solarguard.vo.ProductTokenPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class ProductTokenServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductTokenService productTokenService;

    @Test
    @WithBrowser("admin")
    public void dataIntegrityTest() {
        long productId = organizationService.addProduct(100, ProductPostVO.builder().name("ProductTokenServiceTest.dataIntegrityTest.product").build()).getId();

        Assertions.assertTrue(productService.getTokens(productId).isEmpty());
        long productTokenId = productService.createToken(productId, ProductTokenPostVO.builder().note("ProductTokenServiceTest.dataIntegrityTest.product.token.1").build()).getId();
        Assertions.assertFalse(productService.getTokens(productId).isEmpty());

        ProductTokenEntity productToken = productTokenService.getProductToken(productTokenId);
        Assertions.assertEquals("ProductTokenServiceTest.dataIntegrityTest.product.token.1", productToken.getNote());
        Assertions.assertFalse(productToken.getIsRevoked());
        Assertions.assertNull(productToken.getRevokedAt());
        Assertions.assertNull(productToken.getRevokedBy());

        productToken = productTokenService.patchProductToken(productTokenId, ProductTokenPatchVO.builder().note("ProductTokenServiceTest.dataIntegrityTest.product.token.2").revoke(true).build());
        Assertions.assertEquals("ProductTokenServiceTest.dataIntegrityTest.product.token.2", productToken.getNote());
        Assertions.assertTrue(productToken.getIsRevoked());
        Assertions.assertNotNull(productToken.getRevokedAt());
        Assertions.assertEquals(UserUtil.getId(), productToken.getRevokedBy());

        productTokenService.deleteProductToken(productTokenId);

        Assertions.assertTrue(productService.getTokens(productId).isEmpty());
        Assertions.assertThrows(ProductTokenNotFoundException.class, () -> {
            productTokenService.getProductToken(productTokenId);
        });
    }

}
