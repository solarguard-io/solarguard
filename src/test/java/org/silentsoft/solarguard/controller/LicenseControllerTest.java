package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void getDeviceWithoutAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void getDeviceWithBrowserAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithEmptyDeviceCodeWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", " ")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(203)
    public void getDeviceWithNotExistPackageWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isPreconditionRequired());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithInvalidKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", "JX7CHTKHJJ")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithExpiredKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-EEEEE-EEEEE-EEEEE", "JX7CHTKHJJ")).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithRevokedKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-RRRRR-RRRRR-RRRRR", "JX7CHTKHJJ")).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithDeviceLimitExceededKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-DDDDD-DDDDD-DDDDD", "JX7CHTKHJJ")).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithBannedDeviceWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00001-F6XNE-7HDDF-WTCP7", "3CH4Y3D6X7")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(201)
    public void getDeviceWithOtherProductWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(999)
    public void getDeviceWithInvalidProductWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void getDeviceWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}/devices/{deviceCode}", "TEST0-00000-X6DFC-8BAEM-8RPY3", "JX7CHTKHJJ")).andExpect(status().isOk());
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
        mvc.perform(patch("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-DDDDD-DDDDD-DDDDD", "JX7CHTKHJJ")
                .content(new ObjectMapper().writeValueAsString(DevicePatchVO.builder().name("MacBook-Pro").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isPaymentRequired());
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
        mvc.perform(delete("/api/licenses/{key}/devices/{deviceCode}", "TEST1-00000-DDDDD-DDDDD-DDDDD", "CYADHF43DE")).andExpect(status().isPaymentRequired());
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
    public void activateWithoutAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void activateWithBrowserAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void activateWithEmptyDeviceCodeWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", " ")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(203)
    public void activateWithNotExistPackageWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isPreconditionRequired());
    }

    @Test
    @WithProduct(200)
    public void activateWithInvalidKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "XXXXX-XXXXX-XXXXX-XXXXX-XXXXX")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(200)
    public void activateWithExpiredKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST1-00000-EEEEE-EEEEE-EEEEE")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void activateWithRevokedKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST1-00000-RRRRR-RRRRR-RRRRR")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void activateWithDeviceLimitExceededKeyWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST1-00000-DDDDD-DDDDD-DDDDD")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isPaymentRequired());
    }

    @Test
    @WithProduct(200)
    public void activateWithBannedDeviceWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST1-00001-F6XNE-7HDDF-WTCP7")
                .param("deviceCode", "3CH4Y3D6X7")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(201)
    public void activateWithOtherProductWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithProduct(999)
    public void activateWithInvalidProductWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void activateWithProductAuthority() throws Exception {
        mvc.perform(get("/api/licenses/{key}", "TEST0-00000-X6DFC-8BAEM-8RPY3")
                .param("deviceCode", "JX7CHTKHJJ")
        ).andExpect(status().isOk());
    }

}
