package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.entity.LicenseType;
import org.silentsoft.solarguard.exception.*;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.silentsoft.solarguard.vo.LicensePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class LicenseServiceTest {

    @Autowired
    private LicenseService licenseService;

    @Test
    @WithProduct(200)
    public void addDeviceTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.addDevice("TEST1-00001-F6XNE-7HDDF-WTCP7", DevicePostVO.builder().build());
        });
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.addDevice("XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", DevicePostVO.builder().name("MacBook-Pro").build());
        });
        Assertions.assertThrows(LicenseExpiredException.class, () -> {
            licenseService.addDevice("TEST1-00000-EEEEE-EEEEE-EEEEE", DevicePostVO.builder().name("MacBook-Pro").build());
        });
        Assertions.assertThrows(LicenseRevokedException.class, () -> {
            licenseService.addDevice("TEST1-00000-RRRRR-RRRRR-RRRRR", DevicePostVO.builder().name("MacBook-Pro").build());
        });
        Assertions.assertThrows(LicenseDeviceLimitExceededException.class, () -> {
            licenseService.addDevice("TEST1-00000-DDDDD-DDDDD-DDDDD", DevicePostVO.builder().name("MacBook-Pro").build());
        });

        DeviceEntity device = licenseService.addDevice("TEST1-00001-F6XNE-7HDDF-WTCP7", DevicePostVO.builder().name(" MacBook-Pro ").build());
        Assertions.assertEquals("MacBook-Pro", device.getName());
        Assertions.assertEquals(1, device.getActivationCount());
        Assertions.assertFalse(device.getIsBanned());
        Assertions.assertNotNull(device.getFirstActivatedAt());
        Assertions.assertNotNull(device.getLastActivatedAt());

        Assertions.assertDoesNotThrow(() -> {
            licenseService.addDevice("TEST1-00001-F6XNE-7HDDF-WTCP7", DevicePostVO.builder().name(" MacBook-Pro ").build());
        });
    }

    @Test
    @WithProduct(200)
    public void patchDeviceTest() {
        String key = "TEST1-00001-F6XNE-7HDDF-WTCP7";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            licenseService.patchDevice(key, "3CH4Y3D6X7", DevicePatchVO.builder().name("MacBook-Pro").build());
        });

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.patchDevice(key, "FFFFFFFFFF", DevicePatchVO.builder().name("MacBook-Pro").build());
        });

        DeviceEntity device = licenseService.addDevice(key, DevicePostVO.builder().name(" MacBook-Pro1 ").build());
        String deviceCode = device.getId().getCode();
        Assertions.assertEquals("MacBook-Pro1", device.getName());
        Assertions.assertEquals(1, device.getActivationCount());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.patchDevice(key, deviceCode, DevicePatchVO.builder().build());
        });

        device = licenseService.patchDevice(key, deviceCode, DevicePatchVO.builder().name("MacBook-Pro2").build());
        Assertions.assertEquals("MacBook-Pro2", device.getName());
        Assertions.assertEquals(2, device.getActivationCount());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceTest() {
        String key = "TEST1-00001-F6XNE-7HDDF-WTCP7";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            licenseService.deleteDevice(key, "3CH4Y3D6X7");
        });

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.deleteDevice(key, "FFFFFFFFFF");
        });

        DeviceEntity device = licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Pro").build());
        String deviceCode = device.getId().getCode();
        Assertions.assertDoesNotThrow(() -> {
            licenseService.deleteDevice(key, deviceCode);
        });
        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.deleteDevice(key, deviceCode);
        });
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceThatDeviceLimitExceededLicenseTest() {
        String key = "TEST1-00002-YJCCY-TDWC6-MXE4X";
        DeviceEntity device = licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Pro").build());
        String deviceCode = device.getId().getCode();
        Assertions.assertThrows(LicenseDeviceLimitExceededException.class, () -> {
            licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Air").build());
        });
        Assertions.assertDoesNotThrow(() -> {
            licenseService.deleteDevice(key, deviceCode);
        });
        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.deleteDevice(key, deviceCode);
        });
        Assertions.assertDoesNotThrow(() -> {
            licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Air").build());
        });
    }

    @Test
    @WithUserDetails
    public void getDevicesAndGetDeviceTest() {
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.getDevices(999);
        });

        List<DeviceEntity> devices = licenseService.getDevices(516);
        Assertions.assertEquals(2, devices.size());
        Assertions.assertTrue(devices.stream().anyMatch(device -> device.getId().getCode().equals("JAA3E43FWJ")));
        Assertions.assertTrue(devices.stream().anyMatch(device -> device.getId().getCode().equals("BM7JCP8MYA")));

        Assertions.assertNotNull(licenseService.getDevice(516, "JAA3E43FWJ"));
        Assertions.assertNotNull(licenseService.getDevice(516, "BM7JCP8MYA"));

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.getDevice(516, "FFFFFFFFFF");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.getDevice(516, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.getDevice(516, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.getDevice(516, " ");
        });
    }

    @Test
    @WithUserDetails
    public void deleteDevicesTest() {
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.deleteDevices(999);
        });

        Assertions.assertEquals(2, licenseService.getDevices(517).size());
        licenseService.deleteDevices(517);
        Assertions.assertEquals(0, licenseService.getDevices(517).size());
    }

    @Test
    @WithUserDetails
    public void banDeviceAndUnbanDeviceTest() {
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.banDevice(999, "AR87FXWK4M");
        });
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.unbanDevice(999, "AR87FXWK4M");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.banDevice(518, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.unbanDevice(518, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.banDevice(518, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.unbanDevice(518, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.banDevice(518, " ");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.unbanDevice(518, " ");
        });
        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.banDevice(518, "FFFFFFFFFF");
        });
        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.unbanDevice(518, "FFFFFFFFFF");
        });

        List<DeviceEntity> devices = licenseService.getDevices(518);
        Assertions.assertEquals(2, devices.size());
        Assertions.assertFalse(devices.get(0).getIsBanned());
        Assertions.assertFalse(devices.get(1).getIsBanned());

        licenseService.banDevice(518, "AR87FXWK4M");
        devices = licenseService.getDevices(518);
        Assertions.assertTrue(devices.get(0).getIsBanned());
        Assertions.assertFalse(devices.get(1).getIsBanned());

        licenseService.banDevice(518, "YJWXW4PNRB");
        devices = licenseService.getDevices(518);
        Assertions.assertTrue(devices.get(0).getIsBanned());
        Assertions.assertTrue(devices.get(1).getIsBanned());

        licenseService.unbanDevice(518, "AR87FXWK4M");
        devices = licenseService.getDevices(518);
        Assertions.assertFalse(devices.get(0).getIsBanned());
        Assertions.assertTrue(devices.get(1).getIsBanned());

        licenseService.unbanDevice(518, "YJWXW4PNRB");
        devices = licenseService.getDevices(518);
        Assertions.assertFalse(devices.get(0).getIsBanned());
        Assertions.assertFalse(devices.get(1).getIsBanned());
    }

    @Test
    @WithUserDetails
    public void patchLicenseTest() {
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.patchLicense(999, LicensePatchVO.builder().build());
        });

        LicenseEntity license = licenseService.getLicense(519);
        Assertions.assertEquals(LicenseType.PERPETUAL, license.getType());
        Assertions.assertNull(license.getExpiredAt());
        Assertions.assertFalse(license.getIsDeviceLimited());
        Assertions.assertEquals(1, license.getDeviceLimit());
        Assertions.assertNull(license.getNote());
        Assertions.assertFalse(license.getIsRevoked());
        Assertions.assertNull(license.getRevokedAt());
        Assertions.assertNull(license.getRevokedBy());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.patchLicense(519, LicensePatchVO.builder().licenseType(LicenseType.SUBSCRIPTION).build());
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.patchLicense(519, LicensePatchVO.builder().isDeviceLimited(true).deviceLimit(0L).build());
        });

        licenseService.patchLicense(519, LicensePatchVO.builder()
                        .licenseType(LicenseType.SUBSCRIPTION)
                        .expiredAt(LocalDate.of(9999, 12, 31))
                        .isDeviceLimited(true)
                        .deviceLimit(5L)
                        .note(" patch ")
                        .isRevoked(true)
                        .build()
        );
        license = licenseService.getLicense(519);
        Assertions.assertEquals(LicenseType.SUBSCRIPTION, license.getType());
        Assertions.assertEquals(LocalDate.of(9999, 12, 31), license.getExpiredAt().toLocalDate());
        Assertions.assertTrue(license.getIsDeviceLimited());
        Assertions.assertEquals(5, license.getDeviceLimit());
        Assertions.assertEquals("patch", license.getNote());
        Assertions.assertTrue(license.getIsRevoked());
        Assertions.assertEquals(UserUtil.getId(), license.getRevokedBy());
        Assertions.assertEquals(LocalDate.now(), license.getRevokedAt().toLocalDateTime().toLocalDate());

        licenseService.patchLicense(519, LicensePatchVO.builder().isRevoked(false).build());
        license = licenseService.getLicense(519);
        Assertions.assertFalse(license.getIsRevoked());
        Assertions.assertNull(license.getRevokedAt());
        Assertions.assertNull(license.getRevokedBy());
    }

    @Test
    @WithUserDetails
    public void deleteLicenseTest() {
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.deleteLicense(999);
        });
        Assertions.assertDoesNotThrow(() -> {
            licenseService.getLicense(520);
        });
        licenseService.deleteLicense(520);
        Assertions.assertThrows(LicenseNotFoundException.class, () -> {
            licenseService.getLicense(520);
        });
    }

}
