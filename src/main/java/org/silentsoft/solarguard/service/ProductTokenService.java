package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.exception.ProductTokenNotFoundException;
import org.silentsoft.solarguard.repository.ProductTokenRepository;
import org.silentsoft.solarguard.repository.ProductTokenStatisticsRepository;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.ProductTokenPatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Objects;

@Service
public class ProductTokenService {

    @Autowired
    private ProductTokenRepository productTokenRepository;

    @Autowired
    private ProductTokenStatisticsRepository productTokenStatisticsRepository;

    @Autowired
    private OrganizationUtil organizationUtil;

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public ProductTokenEntity getProductToken(long productTokenId) {
        checkMemberAuthority(productTokenId);

        return findProductToken(productTokenId);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public ProductTokenEntity patchProductToken(long productTokenId, ProductTokenPatchVO productToken) {
        checkStaffAuthority(productTokenId);

        long userId = UserUtil.getId();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        ProductTokenEntity entity = findProductToken(productTokenId);
        if (StringUtils.hasLength(productToken.getNote())) {
            entity.setNote(productToken.getNote());
        }
        if (Objects.nonNull(productToken.getRevoke())) {
            entity.setIsRevoked(productToken.getRevoke());
            if (productToken.getRevoke()) {
                entity.setRevokedAt(now);
                entity.setRevokedBy(userId);
            } else {
                entity.setRevokedAt(null);
                entity.setRevokedBy(null);
            }
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(now);
        entity = productTokenRepository.save(entity);
        return entity;
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @Transactional
    public void deleteProductToken(long productTokenId) {
        checkStaffAuthority(productTokenId);

        productTokenStatisticsRepository.deleteAllByProductTokenId(productTokenId);
        productTokenRepository.deleteById(productTokenId);
    }

    private void checkMemberAuthority(long productTokenId) {
        organizationUtil.checkMemberAuthority(findProductToken(productTokenId).getProduct().getOrganization().getId());
    }

    private void checkStaffAuthority(long productTokenId) {
        organizationUtil.checkStaffAuthority(findProductToken(productTokenId).getProduct().getOrganization().getId());
    }

    private ProductTokenEntity findProductToken(long productTokenId) {
        return productTokenRepository.findById(productTokenId).orElseThrow(() -> new ProductTokenNotFoundException(String.format("The product token '%d' does not exist.", productTokenId)));
    }

}
