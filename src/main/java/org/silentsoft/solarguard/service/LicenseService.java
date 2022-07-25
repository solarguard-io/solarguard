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
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;
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

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    public DeviceEntity addDevice(String key, DevicePostVO devicePostVO) {
        requireKey(key);
        if (!StringUtils.hasText(devicePostVO.getName())) {
            throw new IllegalArgumentException("name is required");
        }

        LicenseEntity license = findLicense(key);

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

        LicenseEntity license = findLicense(key);

        DeviceId deviceId = new DeviceId(license, deviceCode);
        DeviceEntity device = deviceRepository.findById(deviceId).orElseThrow(() -> new DeviceNotFoundException(String.format("The device '%s' under the license '%d' with key '%s' is not found.", deviceCode, license.getId(), license.getKey())));
        if (device.getIsBanned()) {
            throw new AccessDeniedException(String.format("The device '%s' under the license '%d' with key '%s' is banned.", deviceCode, license.getId(), license.getKey()));
        }
        device.setName(devicePatchVO.getName().trim());
        device.setActivationCount(device.getActivationCount() + 1);
        device.setLastActivatedAt(new Timestamp(System.currentTimeMillis()));
        return deviceRepository.save(device);
    }

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    public void deleteDevice(String key, String deviceCode) {
        requireKey(key);
        requireDeviceCode(deviceCode);

        deviceRepository.delete(findDevice(findLicense(key), deviceCode));
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

    private LicenseEntity findLicense(String key) {
        long productId = (long) ThreadLocalHolder.get(ThreadLocalKeys.PRODUCT_ID);

        List<BundleEntity> bundles = bundleRepository.findAllById_ProductId(productId);
        if (bundles.isEmpty()) {
            throw new PackageNotFoundException(String.format("No package found for the product '%d'.", productId));
        }
        List<Long> packageIds = bundles.stream().map(BundleEntity::getId).map(BundleId::getPackageId).collect(Collectors.toList());
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
        if (license.getIsDeviceLimited()) {
            if (license.getDeviceLimit() <= deviceRepository.countAllById_LicenseId(license.getId())) {
                throw new LicenseDeviceLimitExceededException(String.format("The license key '%s' is limited to %d devices.", key, license.getDeviceLimit()));
            }
        }

        return license;
    }

    private DeviceEntity findDevice(LicenseEntity license, String code) {
        DeviceId deviceId = new DeviceId(license, code);
        DeviceEntity device = deviceRepository.findById(deviceId).orElseThrow(() -> new DeviceNotFoundException(String.format("The device '%s' under the license '%d' is not found.", code, license.getId())));
        if (device.getIsBanned()) {
            throw new AccessDeniedException(String.format("The device '%s' under the license '%d' is banned.", code, license.getId()));
        }
        return device;
    }

}
