package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.exception.ProductNotFoundException;
import org.silentsoft.solarguard.repository.ProductRepository;
import org.silentsoft.solarguard.repository.ProductTokenRepository;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.ProductPatchVO;
import org.silentsoft.solarguard.vo.ProductTokenPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private OrganizationUtil organizationUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTokenRepository productTokenRepository;

    @PreAuthorize(Authority.Has.Admin)
    public List<ProductEntity> getProducts() {
        return productRepository.findAll();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public ProductEntity getProduct(long productId) {
        checkMemberAuthority(productId);

        return findProduct(productId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public ProductEntity patchProduct(long productId, ProductPatchVO product) {
        checkStaffAuthority(productId);

        if (!StringUtils.hasText(product.getName())) {
            throw new IllegalArgumentException("Product name is required.");
        }

        ProductEntity entity = findProduct(productId);
        entity.setName(product.getName().trim());
        entity.setUpdatedBy(UserUtil.getId());
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        entity = productRepository.save(entity);
        return entity;
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @Transactional
    public void deleteProduct(long productId) {
        checkStaffAuthority(productId);

        productRepository.deleteById(productId);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public List<ProductTokenEntity> getTokens(long productId) {
        checkMemberAuthority(productId);

        return productTokenRepository.findAllByProductId(productId);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public ProductTokenEntity createToken(long productId, ProductTokenPostVO productToken) {
        checkStaffAuthority(productId);

        if (!StringUtils.hasText(productToken.getNote())) {
            throw new IllegalArgumentException("Token note is required.");
        }

        long userId = UserUtil.getId();

        ProductTokenEntity entity = new ProductTokenEntity();
        entity.setProduct(productRepository.getById(productId));
        entity.setAccessToken(jwtTokenUtil.generateProductAccessToken(productId));
        entity.setNote(productToken.getNote().trim());
        entity.setIsRevoked(false);
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
        entity = productTokenRepository.save(entity);

        return entity;
    }

    private void checkMemberAuthority(long productId) {
        organizationUtil.checkMemberAuthority(findProduct(productId).getOrganization().getId());
    }

    private void checkStaffAuthority(long productId) {
        organizationUtil.checkStaffAuthority(findProduct(productId).getOrganization().getId());
    }

    private ProductEntity findProduct(long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(String.format("The product '%d' does not exist.", productId)));
    }

}
