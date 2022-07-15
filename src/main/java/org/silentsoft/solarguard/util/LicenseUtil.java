package org.silentsoft.solarguard.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class LicenseUtil {

    public static final char[] CHARACTERS = "ABCDEFHJKMNPRTWXY34678".toCharArray();

    public static String generateLicenseKey() {
        String licenseKey = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, CHARACTERS, 25);
        return String.join("-", licenseKey.substring(0, 5), licenseKey.substring(5, 10), licenseKey.substring(10, 15), licenseKey.substring(15, 20), licenseKey.substring(20, 25));
    }

    public static String generateDeviceCode() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, CHARACTERS, 10);
    }

}
