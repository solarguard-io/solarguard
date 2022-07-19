package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.*;
import org.silentsoft.solarguard.exception.PackageNotFoundException;
import org.silentsoft.solarguard.repository.*;
import org.silentsoft.solarguard.util.LicenseUtil;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.LicensePostVO;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private DeviceRepository deviceRepository;

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

        if (StringUtils.hasText(packagePatchVO.getName())) {
            packageEntity.setName(packagePatchVO.getName().trim());
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

            bundleRepository.deleteAllByPackageId(packageId);

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
    @Transactional
    public void deletePackage(long packageId) {
        checkStaffAuthority(packageId);

        bundleRepository.deleteAllByPackageId(packageId);

        List<Long> licenseIds = licenseRepository.findAllBy_package(findPackage(packageId)).stream().map(LicenseEntity::getId).collect(Collectors.toList());
        deviceRepository.deleteAllByLicenseIdIn(licenseIds);
        licenseRepository.deleteAllByIdInBatch(licenseIds);

        packageRepository.deleteById(packageId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<BundleEntity> getBundles(long packageId) {
        checkMemberAuthority(packageId);

        return bundleRepository.findAllById_PackageId(packageId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public LicenseEntity issueLicense(long packageId, LicensePostVO licensePostVO) {
        checkMemberAuthority(packageId);

        if (licensePostVO.getLicenseType() == null) {
            throw new IllegalArgumentException("License type is required.");
        }
        if (licensePostVO.getLicenseType() == LicenseType.SUBSCRIPTION) {
            if (licensePostVO.getExpiredAt() == null) {
                throw new IllegalArgumentException("Expiration date is required.");
            } else if (licensePostVO.getExpiredAt().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Expiration date must be after current date.");
            }
        }
        if (licensePostVO.getIsDeviceLimited() != null && licensePostVO.getIsDeviceLimited()) {
            if (licensePostVO.getDeviceLimit() == null) {
                throw new IllegalArgumentException("Device limit is required.");
            } else if (licensePostVO.getDeviceLimit() <= 0) {
                throw new IllegalArgumentException("Device limit must be greater than 0.");
            }
        }

        LicenseEntity licenseEntity = new LicenseEntity();
        licenseEntity.setPackage(findPackage(packageId));
        licenseEntity.setKey(LicenseUtil.generateLicenseKey());
        licenseEntity.setType(licensePostVO.getLicenseType());
        if (licensePostVO.getLicenseType() == LicenseType.SUBSCRIPTION) {
            licenseEntity.setExpiredAt(Date.valueOf(licensePostVO.getExpiredAt()));
        }
        if (licensePostVO.getIsDeviceLimited() != null) {
            licenseEntity.setIsDeviceLimited(licensePostVO.getIsDeviceLimited());
            licenseEntity.setDeviceLimit(licensePostVO.getDeviceLimit());
        }
        if (StringUtils.hasText(licensePostVO.getNote())) {
            licenseEntity.setNote(licensePostVO.getNote().trim());
        }
        licenseEntity.setCreatedBy(UserUtil.getId());
        licenseEntity.setUpdatedBy(UserUtil.getId());
        return licenseRepository.save(licenseEntity);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<LicenseEntity> getLicenses(long packageId) {
        checkMemberAuthority(packageId);

        return licenseRepository.findAllBy_package(findPackage(packageId));
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
