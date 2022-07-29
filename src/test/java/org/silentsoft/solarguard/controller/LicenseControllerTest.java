package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.entity.LicenseType;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.silentsoft.solarguard.vo.LicensePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class LicenseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void addDeviceWithoutAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void addDeviceWithBrowserAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithoutNameWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithEmptyNameWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name(" ").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(203)
    public void addDeviceWithNotExistPackageWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPreconditionRequired());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithInvalidKeyWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithExpiredKeyWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST1-00000-EEEEE-EEEEE-EEEEE")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithRevokedKeyWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST1-00000-RRRRR-RRRRR-RRRRR")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithDeviceLimitExceededKeyWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST1-00000-DDDDD-DDDDD-DDDDD")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(201)
    public void addDeviceWithOtherProductWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(999)
    public void addDeviceWithInvalidProductWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void addDeviceWithProductAuthority() throws Exception {
        mvc.perform(post("/api/licenses/{key}/devices", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .content(new ObjectMapper().writeValueAsString(DevicePostVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    public void patchDeviceWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void patchDeviceWithBrowserAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithEmptyDeviceCodeWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", " ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(203)
    public void patchDeviceWithNotExistPackageWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPreconditionRequired());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithInvalidKeyWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithExpiredKeyWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-EEEEE-EEEEE-EEEEE", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithRevokedKeyWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-RRRRR-RRRRR-RRRRR", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithDeviceLimitExceededKeyWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-DDDDD-DDDDD-DDDDD", "68PKNXBD6K")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithBannedDeviceWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00001-F6XNE-7HDDF-WTCP7", "3CH4Y3D6X7")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(201)
    public void patchDeviceWithOtherProductWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(999)
    public void patchDeviceWithInvalidProductWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void patchDeviceWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void deleteDeviceWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void deleteDeviceWithBrowserAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithEmptyDeviceCodeWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", " ")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(203)
    public void deleteDeviceWithNotExistPackageWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isPreconditionRequired());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithInvalidKeyWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", "CYADHF43DE")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithExpiredKeyWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-EEEEE-EEEEE-EEEEE", "CYADHF43DE")).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithRevokedKeyWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-RRRRR-RRRRR-RRRRR", "CYADHF43DE")).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithDeviceLimitExceededKeyWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00002-AEDXR-4PTK6-Y3J67", "RWB847W4N3")).andExpect(status().isNoContent());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithBannedDeviceWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00001-F6XNE-7HDDF-WTCP7", "3CH4Y3D6X7")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(201)
    public void deleteDeviceWithOtherProductWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(999)
    public void deleteDeviceWithInvalidProductWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void deleteDeviceWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "CYADHF43DE")).andExpect(status().isNoContent());
    }

    @Test
    public void getLicenseWithoutAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}", "500")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void getLicenseWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}", "500")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void getLicenseWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}", "500")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getLicenseWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}", "500")).andExpect(status().isOk());
    }

    @Test
    public void patchLicenseWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{licenseId}", "500")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void patchLicenseWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{licenseId}", "500")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void patchLicenseWithNonMemberAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{licenseId}", "500")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void patchLicenseWithMemberAuthority() throws Exception {
        mvc.perform(patch("/api/licenses/{licenseId}", "999")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}", "524")).andExpect(status().isOk()).andDo(result -> {
            LicenseEntity license = new ObjectMapper().readValue(result.getResponse().getContentAsString(), LicenseEntity.class);
            Assertions.assertEquals(LicenseType.PERPETUAL, license.getType());
            Assertions.assertNull(license.getExpiredAt());
            Assertions.assertFalse(license.getIsDeviceLimited());
            Assertions.assertEquals(1, license.getDeviceLimit());
            Assertions.assertNull(license.getNote());
            Assertions.assertFalse(license.getIsRevoked());
            Assertions.assertNull(license.getRevokedAt());
            Assertions.assertNull(license.getRevokedBy());
        });

        mvc.perform(patch("/api/licenses/{licenseId}", "524")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().licenseType(LicenseType.SUBSCRIPTION).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(patch("/api/licenses/{licenseId}", "524")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().deviceLimited(true).deviceLimit(0L).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(patch("/api/licenses/{licenseId}", "524")
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(LicensePatchVO.builder()
                        .licenseType(LicenseType.SUBSCRIPTION)
                        .expiredAt(LocalDate.of(9999, 12, 31))
                        .deviceLimited(true)
                        .deviceLimit(5L)
                        .note(" patch ")
                        .revoke(true)
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mvc.perform(get("/api/licenses/{licenseId}", "524")).andExpect(status().isOk()).andDo(result -> {
            LicenseEntity license = new ObjectMapper().readValue(result.getResponse().getContentAsString(), LicenseEntity.class);
            Assertions.assertEquals(LicenseType.SUBSCRIPTION, license.getType());
            Assertions.assertEquals(LocalDate.of(9999, 12, 31), license.getExpiredAt().toLocalDate());
            Assertions.assertTrue(license.getIsDeviceLimited());
            Assertions.assertEquals(5, license.getDeviceLimit());
            Assertions.assertEquals("patch", license.getNote());
            Assertions.assertTrue(license.getIsRevoked());
            Assertions.assertNotNull(license.getRevokedBy());
            Assertions.assertEquals(LocalDate.now(), license.getRevokedAt().toLocalDateTime().toLocalDate());
        });

        mvc.perform(patch("/api/licenses/{licenseId}", "524")
                .content(new ObjectMapper().writeValueAsString(LicensePatchVO.builder().revoke(false).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mvc.perform(get("/api/licenses/{licenseId}", "524")).andExpect(status().isOk()).andDo(result -> {
            LicenseEntity license = new ObjectMapper().readValue(result.getResponse().getContentAsString(), LicenseEntity.class);
            Assertions.assertFalse(license.getIsRevoked());
            Assertions.assertNull(license.getRevokedAt());
            Assertions.assertNull(license.getRevokedBy());
        });
    }

    @Test
    public void deleteLicenseWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}", "500")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void deleteLicenseWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}", "500")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void deleteLicenseWithNonMemberAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}", "500")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void deleteLicenseWithMemberAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}", "999")).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}", "525")).andExpect(status().isOk());
        mvc.perform(delete("/api/licenses/{licenseId}", "525")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}", "525")).andExpect(status().isNotFound());
    }

    @Test
    public void getDevicesAndGetDeviceWithoutAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}/devices", "521")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void getDevicesAndGetDeviceWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}/devices", "521")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void getDevicesAndGetDeviceWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}/devices", "521")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getDevicesAndGetDeviceWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{licenseId}/devices", "999")).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}/devices", "521")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("8JXRYP6KAY"))
                .andExpect(jsonPath("$[1].code").value("FJAE6M3HT3"));

        mvc.perform(get("/api/licenses/{licenseId}/devices/{deviceCode}", "521", "8JXRYP6KAY")).andExpect(status().isOk());
        mvc.perform(get("/api/licenses/{licenseId}/devices/{deviceCode}", "521", "FJAE6M3HT3")).andExpect(status().isOk());

        mvc.perform(get("/api/licenses/{licenseId}/devices/{deviceCode}", "521", "FFFFFFFFFF")).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}/devices/{deviceCode}", "521", " ")).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteDevicesWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void deleteDevicesWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void deleteDevicesWithNonMemberAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void deleteDevicesWithMemberAuthority() throws Exception {
        mvc.perform(delete("/api/licenses/{licenseId}/devices", "999")).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("64MDWW74MA"))
                .andExpect(jsonPath("$[1].code").value("4CWYTPEKK3"));
        mvc.perform(delete("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}/devices", "522")).andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void banDeviceAndUnbanDeviceWithoutAuthority() throws Exception {
        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void banDeviceAndUnbanDeviceWithProductAuthority() throws Exception {
        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test3")
    public void banDeviceAndUnbanDeviceWithNonMemberAuthority() throws Exception {
        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void banDeviceAndUnbanDeviceWithMemberAuthority() throws Exception {
        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "999", "R3NYXPE883")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "999", "R3NYXPE883")).andExpect(status().isNotFound());

        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", " ")).andExpect(status().isUnprocessableEntity());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", " ")).andExpect(status().isUnprocessableEntity());

        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "FFFFFFFFFF")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "FFFFFFFFFF")).andExpect(status().isNotFound());

        mvc.perform(get("/api/licenses/{licenseId}/devices", "523")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R3NYXPE883"))
                .andExpect(jsonPath("$[0].isBanned").value("false"))
                .andExpect(jsonPath("$[1].code").value("RXF8MPKYRH"))
                .andExpect(jsonPath("$[1].isBanned").value("false"));

        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}/devices", "523")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R3NYXPE883"))
                .andExpect(jsonPath("$[0].isBanned").value("true"))
                .andExpect(jsonPath("$[1].code").value("RXF8MPKYRH"))
                .andExpect(jsonPath("$[1].isBanned").value("false"));

        mvc.perform(put("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "RXF8MPKYRH")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}/devices", "523")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R3NYXPE883"))
                .andExpect(jsonPath("$[0].isBanned").value("true"))
                .andExpect(jsonPath("$[1].code").value("RXF8MPKYRH"))
                .andExpect(jsonPath("$[1].isBanned").value("true"));

        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "R3NYXPE883")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}/devices", "523")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R3NYXPE883"))
                .andExpect(jsonPath("$[0].isBanned").value("false"))
                .andExpect(jsonPath("$[1].code").value("RXF8MPKYRH"))
                .andExpect(jsonPath("$[1].isBanned").value("true"));

        mvc.perform(delete("/api/licenses/{licenseId}/devices/{deviceCode}/ban", "523", "RXF8MPKYRH")).andExpect(status().isNoContent());
        mvc.perform(get("/api/licenses/{licenseId}/devices", "523")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R3NYXPE883"))
                .andExpect(jsonPath("$[0].isBanned").value("false"))
                .andExpect(jsonPath("$[1].code").value("RXF8MPKYRH"))
                .andExpect(jsonPath("$[1].isBanned").value("false"));
    }

}
