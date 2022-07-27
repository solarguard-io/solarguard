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

}
