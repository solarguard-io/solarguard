package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.core.context.ThreadLocalHolder;
import org.silentsoft.solarguard.core.context.ThreadLocalKeys;
import org.silentsoft.solarguard.entity.*;
import org.silentsoft.solarguard.exception.*;
import org.silentsoft.solarguard.repository.BundleRepository;
import org.silentsoft.solarguard.repository.DeviceRepository;
import org.silentsoft.solarguard.repository.LicenseRepository;
import org.silentsoft.solarguard.repository.PackageRepository;
import org.silentsoft.solarguard.util.LicenseUtil;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.silentsoft.solarguard.vo.LicensePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganizationUtil organizationUtil;

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    public DeviceEntity addDevice(String key, DevicePostVO devicePostVO) {
        requireKey(key);
        if (!StringUtils.hasText(devicePostVO.getName())) {
            throw new IllegalArgumentException("name is required");
        }

        LicenseEntity license = findLicenseByKey(key);
        if (license.getIsDeviceLimited()) {
            if (license.getDeviceLimit() <= deviceRepository.countAllById_LicenseId(license.getId())) {
                throw new LicenseDeviceLimitExceededException(String.format("The license key '%s' is limited to %d devices.", key, license.getDeviceLimit()));
            }
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        DeviceEntity device = new DeviceEntity();
        device.setId(new DeviceId(license, LicenseUtil.generateDeviceCode()));
        device.setName(devicePostVO.getName().trim());
        device.setActivationCount(1L);
        device.setIsBanned(false);
        device.setFirstActivatedAt(now);
        device.setLastActivatedAt(now);
        return deviceRepository.save(device);
    }

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    public DeviceEntity patchDevice(String key, String deviceCode, DevicePatchVO devicePatchVO) {
        requireKey(key);
        requireDeviceCode(deviceCode);
        if (!StringUtils.hasText(devicePatchVO.getName())) {
            throw new IllegalArgumentException("name is required");
        }

        DeviceEntity device = findDevice(findLicenseByKey(key), deviceCode, true);
        device.setName(devicePatchVO.getName().trim());
        device.setActivationCount(device.getActivationCount() + 1);
        device.setLastActivatedAt(new Timestamp(System.currentTimeMillis()));
        return deviceRepository.save(device);
    }

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    public void deleteDevice(String key, String deviceCode) {
        requireKey(key);
        requireDeviceCode(deviceCode);

        deviceRepository.delete(findDevice(findLicenseByKey(key), deviceCode, true));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public LicenseEntity getLicense(long licenseId) {
        checkMemberAuthority(licenseId);

        return findLicenseById(licenseId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public LicenseEntity patchLicense(long licenseId, LicensePatchVO licensePatchVO) {
        checkMemberAuthority(licenseId);

        long userId = UserUtil.getId();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        LicenseEntity license = findLicenseById(licenseId);
        if (Objects.nonNull(licensePatchVO.getLicenseType())) {
            license.setType(licensePatchVO.getLicenseType());
        }
        if (Objects.nonNull(licensePatchVO.getExpiredAt())) {
            license.setExpiredAt(Date.valueOf(licensePatchVO.getExpiredAt()));
        }
        if (license.getType() == LicenseType.SUBSCRIPTION && Objects.isNull(license.getExpiredAt())) {
            throw new IllegalArgumentException("Expiration date is required.");
        }
        if (Objects.nonNull(licensePatchVO.getIsDeviceLimited())) {
            license.setIsDeviceLimited(licensePatchVO.getIsDeviceLimited());
        }
        if (Objects.nonNull(licensePatchVO.getDeviceLimit())) {
            if (licensePatchVO.getDeviceLimit() <= 0) {
                throw new IllegalArgumentException("Device limit must be greater than 0.");
            }
            license.setDeviceLimit(licensePatchVO.getDeviceLimit());
        }
        if (StringUtils.hasText(licensePatchVO.getNote())) {
            license.setNote(licensePatchVO.getNote().trim());
        }
        if (Objects.nonNull(licensePatchVO.getIsRevoked())) {
            license.setIsRevoked(licensePatchVO.getIsRevoked());
            if (licensePatchVO.getIsRevoked()) {
                license.setRevokedBy(userId);
                license.setRevokedAt(now);
            } else {
                license.setRevokedBy(null);
                license.setRevokedAt(null);
            }
        }
        license.setUpdatedBy(userId);
        license.setUpdatedAt(now);
        return licenseRepository.save(license);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void deleteLicense(long licenseId) {
        checkMemberAuthority(licenseId);

        licenseRepository.deleteById(licenseId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<DeviceEntity> getDevices(long licenseId) {
        checkMemberAuthority(licenseId);

        return deviceRepository.findAllByLicenseId(licenseId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public DeviceEntity getDevice(long licenseId, String deviceCode) {
        requireDeviceCode(deviceCode);
        checkMemberAuthority(licenseId);

        return findDevice(findLicenseById(licenseId), deviceCode, false);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @Transactional
    public void deleteDevices(long licenseId) {
        checkMemberAuthority(licenseId);

        deviceRepository.deleteAllByLicense(findLicenseById(licenseId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void banDevice(long licenseId, String deviceCode) {
        requireDeviceCode(deviceCode);
        checkMemberAuthority(licenseId);

        DeviceEntity device = findDevice(findLicenseById(licenseId), deviceCode, false);
        device.setIsBanned(true);
        deviceRepository.save(device);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void unbanDevice(long licenseId, String deviceCode) {
        requireDeviceCode(deviceCode);
        checkMemberAuthority(licenseId);

        DeviceEntity device = findDevice(findLicenseById(licenseId), deviceCode, false);
        device.setIsBanned(false);
        deviceRepository.save(device);
    }

    private void requireKey(String key) {
        requireHasText(key, "key is required");
    }

    private void requireDeviceCode(String deviceCode) {
        requireHasText(deviceCode, "deviceCode is required");
    }

    private void requireHasText(String value, String exceptionMessage) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    private LicenseEntity findLicenseByKey(String key) {
        long productId = (long) ThreadLocalHolder.get(ThreadLocalKeys.PRODUCT_ID);

        List<BundleEntity> bundles = bundleRepository.findAllByProductId(productId);
        if (bundles.isEmpty()) {
            throw new PackageNotFoundException(String.format("No package found for the product '%d'.", productId));
        }
        List<Long> packageIds = bundles.stream().map(BundleEntity::getId).map(BundleId::getPackage).map(PackageEntity::getId).collect(Collectors.toList());
        List<PackageEntity> packages = packageRepository.findAllById(packageIds);

        LicenseEntity license = licenseRepository.findBy_packageInAndKey(packages, key);
        if (license == null) {
            throw new LicenseNotFoundException(String.format("The license key '%s' does not exist.", key));
        }
        if (license.getType() == LicenseType.SUBSCRIPTION && license.getExpiredAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new LicenseExpiredException(String.format("The license key '%s' is expired.", key));
        }
        if (license.getIsRevoked()) {
            throw new LicenseRevokedException(String.format("The license key '%s' is revoked.", key));
        }

        return license;
    }

    private LicenseEntity findLicenseById(long licenseId) {
        return licenseRepository.findById(licenseId).orElseThrow(() -> new LicenseNotFoundException(String.format("The license '%d' is not found.", licenseId)));
    }

    private DeviceEntity findDevice(LicenseEntity license, String code, boolean shouldThrowExceptionIfBanned) {
        DeviceId deviceId = new DeviceId(license, code);
        DeviceEntity device = deviceRepository.findById(deviceId).orElseThrow(() -> new DeviceNotFoundException(String.format("The device '%s' under the license '%d' is not found.", code, license.getId())));
        if (device.getIsBanned() && shouldThrowExceptionIfBanned) {
            throw new AccessDeniedException(String.format("The device '%s' under the license '%d' is banned.", code, license.getId()));
        }
        return device;
    }

    private void checkMemberAuthority(long licenseId) {
        organizationUtil.checkMemberAuthority(findLicenseById(licenseId).getPackage().getOrganization().getId());
    }

}
