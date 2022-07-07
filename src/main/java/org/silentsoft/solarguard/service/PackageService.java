package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.BundleEntity;
import org.silentsoft.solarguard.entity.BundleId;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.exception.PackageNotFoundException;
import org.silentsoft.solarguard.repository.BundleRepository;
import org.silentsoft.solarguard.repository.PackageRepository;
import org.silentsoft.solarguard.repository.ProductRepository;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private OrganizationUtil organizationUtil;

    @PreAuthorize(Authority.Has.Admin)
    public List<PackageEntity> getPackages() {
        return packageRepository.findAll();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public PackageEntity getPackage(long packageId) {
        checkMemberAuthority(packageId);

        return findPackage(packageId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @Transactional
    public PackageEntity patchPackage(long packageId, PackagePatchVO packagePatchVO) {
        checkStaffAuthority(packageId);

        PackageEntity packageEntity = findPackage(packageId);
        long userId = UserUtil.getId();
        long organizationId = packageEntity.getOrganization().getId();

        if (StringUtils.hasLength(packagePatchVO.getName())) {
            packageEntity.setName(packagePatchVO.getName());
            packageEntity.setUpdatedBy(userId);
            packageEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            packageEntity = packageRepository.save(packageEntity);
        }

        if (Objects.nonNull(packagePatchVO.getProductIds())) {
            packagePatchVO.getProductIds().removeIf(Objects::isNull);
            boolean hasDuplicateProduct = packagePatchVO.getProductIds().stream().distinct().count() < packagePatchVO.getProductIds().size();
            if (hasDuplicateProduct) {
                throw new IllegalArgumentException("Duplicate products are not allowed.");
            }

            List<ProductEntity> products = productRepository.findAllById(packagePatchVO.getProductIds());

            boolean hasNotExistProduct = products.size() != packagePatchVO.getProductIds().size();
            if (hasNotExistProduct) {
                throw new IllegalArgumentException("Some products are not exist.");
            }

            boolean hasProductNotBelongToOrganization = products.stream().anyMatch(product -> product.getOrganization().getId() != organizationId);
            if (hasProductNotBelongToOrganization) {
                throw new IllegalArgumentException("Some products are not owned by this organization.");
            }

            bundleRepository.deleteAllById_PackageId(packageId);

            for (ProductEntity product : products) {
                BundleEntity bundle = new BundleEntity();
                bundle.setId(new BundleId(packageEntity.getId(), product.getId()));
                bundle.setCreatedBy(userId);
                bundle.setUpdatedBy(userId);
                bundleRepository.save(bundle);
            }
        }

        return packageEntity;
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void deletePackage(long packageId) {
        checkStaffAuthority(packageId);

        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<BundleEntity> getBundles(long packageId) {
        checkMemberAuthority(packageId);

        return bundleRepository.findAllById_PackageId(packageId);
    }

    private void checkMemberAuthority(long packageId) {
        organizationUtil.checkMemberAuthority(findPackage(packageId).getOrganization().getId());
    }

    private void checkStaffAuthority(long packageId) {
        organizationUtil.checkStaffAuthority(findPackage(packageId).getOrganization().getId());
    }

    private PackageEntity findPackage(long packageId) {
        return packageRepository.findById(packageId).orElseThrow(() -> new PackageNotFoundException(String.format("The package '%d' does not exist.", packageId)));
    }

}
