package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.exception.*;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

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
        Assertions.assertEquals(0, device.getActivationCount());
        Assertions.assertFalse(device.getIsBanned());
        Assertions.assertNull(device.getFirstActivatedAt());
        Assertions.assertNull(device.getLastActivatedAt());
    }

    @Test
    @WithProduct(200)
    public void getDeviceTest() {
        String key = "TEST1-00001-F6XNE-7HDDF-WTCP7";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            licenseService.getDevice(key, "3CH4Y3D6X7");
        });

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.getDevice(key, "FFFFFFFFFF");
        });

        String deviceCode = licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Air").build()).getId().getCode();
        DeviceEntity device = licenseService.getDevice(key, deviceCode);
        Assertions.assertEquals("MacBook-Air", device.getName());
        Assertions.assertEquals(0, device.getActivationCount());
        Assertions.assertFalse(device.getIsBanned());
        Assertions.assertNull(device.getFirstActivatedAt());
        Assertions.assertNull(device.getLastActivatedAt());
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
        Assertions.assertEquals(0, device.getActivationCount());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.patchDevice(key, deviceCode, DevicePatchVO.builder().build());
        });

        device = licenseService.patchDevice(key, deviceCode, DevicePatchVO.builder().name("MacBook-Pro2").build());
        Assertions.assertEquals("MacBook-Pro2", device.getName());
        Assertions.assertEquals(0, device.getActivationCount());
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
            licenseService.getDevice(key, deviceCode);
        });

        licenseService.deleteDevice(key, deviceCode);

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.getDevice(key, deviceCode);
        });
    }

    @Test
    @WithProduct(200)
    public void activateTest() {
        String key = "TEST1-00001-F6XNE-7HDDF-WTCP7";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            licenseService.activate(key, "3CH4Y3D6X7");
        });

        Assertions.assertThrows(DeviceNotFoundException.class, () -> {
            licenseService.activate(key, "FFFFFFFFFF");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.activate(key, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.activate(key, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            licenseService.activate(key, " ");
        });

        DeviceEntity device = licenseService.addDevice(key, DevicePostVO.builder().name("MacBook-Pro").build());
        Assertions.assertEquals(0, device.getActivationCount());

        LicenseEntity license = licenseService.activate(key, device.getId().getCode());
        Assertions.assertEquals(key, license.getKey());

        device = licenseService.getDevice(key, device.getId().getCode());
        Assertions.assertEquals(1, device.getActivationCount());
        Assertions.assertNotNull(device.getFirstActivatedAt());
        Assertions.assertNotNull(device.getLastActivatedAt());

        licenseService.activate(key, device.getId().getCode());
        device = licenseService.getDevice(key, device.getId().getCode());
        Assertions.assertEquals(2, device.getActivationCount());
    }

}
