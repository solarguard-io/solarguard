package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.LicenseType;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.vo.LicensePostVO;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.silentsoft.solarguard.vo.PackagePostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class PackageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getPackagesWithoutAuthority() throws Exception {
        mvc.perform(get("/api/packages")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getPackagesWithUserAuthority() throws Exception {
        mvc.perform(get("/api/packages")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getPackagesWithProductAuthority() throws Exception {
        mvc.perform(get("/api/packages")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void getPackagesWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/packages")).andExpect(status().isOk());
    }

    @Test
    public void getPackageWithoutAuthority() throws Exception {
        mvc.perform(get("/api/packages/300")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getPackageWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test3")
    public void getPackageWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getPackageWithProductAuthority() throws Exception {
        mvc.perform(get("/api/packages/300")).andExpect(status().isForbidden());
    }

    @Test
    public void patchPackageWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().name("A2 Package").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void patchPackageWithMemberAuthority() throws Exception {
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().name("A2 Package").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void patchPackageWithStaffAuthority() throws Exception {
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().name("A2 Package").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Duplicate product Ids are not allowed.
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().productIds(Arrays.asList(200L, 200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Some products are not exists.
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().productIds(Arrays.asList(200L, 999L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Some products are not owned by this organization.
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().productIds(Arrays.asList(200L, 201L, 203L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithProduct(200)
    public void patchPackageWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/packages/300")
                .content(new ObjectMapper().writeValueAsString(PackagePatchVO.builder().name("A2 Package").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void deletePackageWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/packages/300")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void deletePackageWithMemberAuthority() throws Exception {
        mvc.perform(delete("/api/packages/300")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void deletePackageWithStaffAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 201L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andDo(result -> {
            PackageEntity packageEntity = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PackageEntity.class);
            mvc.perform(delete("/api/packages/{packageId}", packageEntity.getId())).andExpect(status().isNoContent());
        });
    }

    @Test
    @WithProduct(200)
    public void deletePackageWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/packages/300")).andExpect(status().isForbidden());
    }

    @Test
    public void getBundlesWithoutAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/bundles")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getBundlesWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/bundles")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test3")
    public void getBundlesWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/bundles")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getBundlesWithProductAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/bundles")).andExpect(status().isForbidden());
    }

    @Test
    public void issueLicenseWithoutAuthority() throws Exception {
        mvc.perform(post("/api/packages/300/licenses")
                .content(new ObjectMapper().writeValueAsString(LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void issueLicenseWithMemberAuthority() throws Exception {
        mvc.perform(post("/api/packages/300/licenses")
                .content(new ObjectMapper().writeValueAsString(LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("test3")
    public void issueLicenseWithNonMemberAuthority() throws Exception {
        mvc.perform(post("/api/packages/300/licenses")
                .content(new ObjectMapper().writeValueAsString(LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void issueLicenseWithProductAuthority() throws Exception {
        mvc.perform(post("/api/packages/300/licenses")
                .content(new ObjectMapper().writeValueAsString(LicensePostVO.builder().licenseType(LicenseType.PERPETUAL).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void getLicensesWithoutAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/licenses")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getLicensesWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/licenses")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test3")
    public void getLicensesWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/licenses")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getLicensesWithProductAuthority() throws Exception {
        mvc.perform(get("/api/packages/300/licenses")).andExpect(status().isForbidden());
    }

}
