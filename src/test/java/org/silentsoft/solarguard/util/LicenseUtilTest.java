package org.silentsoft.solarguard.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

public class LicenseUtilTest {

    @Test
    public void charactersTest() {
        Assertions.assertArrayEquals("ABCDEFHJKMNPRTWXY34678".toCharArray(), LicenseUtil.CHARACTERS);
    }

    @Test
    public void generateLicenseKeyTest() {
        String licenseKey = LicenseUtil.generateLicenseKey();
        Assertions.assertNotNull(licenseKey);
        Assertions.assertEquals("XXXXX-XXXXX-XXXXX-XXXXX-XXXXX".length(), licenseKey.length());
        Assertions.assertEquals(4, StringUtils.countOccurrencesOf(licenseKey, "-"));
        Assertions.assertEquals('-', licenseKey.charAt(5));
        Assertions.assertEquals('-', licenseKey.charAt(11));
        Assertions.assertEquals('-', licenseKey.charAt(17));
        Assertions.assertEquals('-', licenseKey.charAt(23));
        Assertions.assertTrue(licenseKey.replaceAll("-", "").matches("[ABCDEFHJKMNPRTWXY34678]{25}"));
    }

    @Test
    public void generateDeviceCodeTest() {
        String deviceCode = LicenseUtil.generateDeviceCode();
        Assertions.assertNotNull(deviceCode);
        Assertions.assertEquals("XXXXXXXXXX".length(), deviceCode.length());
        Assertions.assertEquals(-1, deviceCode.indexOf("-"));
        Assertions.assertTrue(deviceCode.matches("[ABCDEFHJKMNPRTWXY34678]{10}"));
    }

}
