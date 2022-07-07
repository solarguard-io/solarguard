package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.OrganizationEntity;
import org.silentsoft.solarguard.vo.*;
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
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getOrganizationsWithoutAuthority() throws Exception {
        mvc.perform(get("/api/organizations")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getOrganizationsWithUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void getOrganizationsWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/organizations")).andExpect(status().isOk());
    }

    @Test
    @WithProduct(200)
    public void getOrganizationsWithProductAuthority() throws Exception {
        mvc.perform(get("/api/organizations")).andExpect(status().isForbidden());
    }

    @Test
    public void getOrganizationWithoutAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}", "100")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getOrganizationWithUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}", "100")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test1")
    public void getOrganizationWithNonUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void getOrganizationWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}", "100")).andExpect(status().isOk());
        mvc.perform(get("/api/organizations/{organizationId}", "200")).andExpect(status().isNotFound());
    }

    @Test
    @WithProduct(200)
    public void getOrganizationWithProductAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}", "100")).andExpect(status().isForbidden());
    }

    @Test
    public void createOrganizationWithoutAuthority() throws Exception {
        mvc.perform(post("/api/organizations")
                .content(new ObjectMapper().writeValueAsString(OrganizationPostVO.builder().name("Test Organization").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void createOrganizationWithProductAuthority() throws Exception {
        mvc.perform(post("/api/organizations")
                .content(new ObjectMapper().writeValueAsString(OrganizationPostVO.builder().name("Test Organization").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void createOrganizationWithUserAuthority() throws Exception {
        // Name is missing
        mvc.perform(post("/api/organizations").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        mvc.perform(post("/api/organizations")
                .content(new ObjectMapper().writeValueAsString(OrganizationPostVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/organizations")
                .content(new ObjectMapper().writeValueAsString(OrganizationPostVO.builder().name("").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/organizations")
                .content(new ObjectMapper().writeValueAsString(OrganizationPostVO.builder().name("Test Organization").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    public void patchOrganizationWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/organizations/{organizationId}", "100")
                .content(new ObjectMapper().writeValueAsString(OrganizationPatchVO.builder().name("Test Organization2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void patchOrganizationWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/organizations/{organizationId}", "100")
                .content(new ObjectMapper().writeValueAsString(OrganizationPatchVO.builder().name("Test Organization2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void patchOrganizationWithMemberAuthority() throws Exception {
        mvc.perform(patch("/api/organizations/{organizationId}", "100")
                .content(new ObjectMapper().writeValueAsString(OrganizationPatchVO.builder().name("Test Organization2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test1")
    public void patchOrganizationWithNonMemberAuthority() throws Exception {
        mvc.perform(patch("/api/organizations/{organizationId}", "100")
                .content(new ObjectMapper().writeValueAsString(OrganizationPatchVO.builder().name("Test Organization2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void patchOrganizationWithStaffAuthority() throws Exception {
        mvc.perform(patch("/api/organizations/{organizationId}", "100")
                .content(new ObjectMapper().writeValueAsString(OrganizationPatchVO.builder().name("Test Organization2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andDo(result -> {
            OrganizationEntity organization = new ObjectMapper().readValue(result.getResponse().getContentAsString(), OrganizationEntity.class);
            Assertions.assertEquals("Test Organization2", organization.getName());
        });
    }

    @Test
    public void getMembersWithoutAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/members", "100")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getMembersWithUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/members", "100")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test1")
    public void getMembersWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/members", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getMembersWithProductAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/members", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getMembersOfNonExistingOrganization() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/members", "200")).andExpect(status().isNotFound());
    }

    @Test
    public void addMembersWithoutAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void removeMembersWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void addMembersWithNonMemberAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void removeMembersWithNonMemberAuthority() throws Exception {
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test1")
    public void addMembersWithNonStaffAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test1")
    public void removeMembersWithNonStaffAuthority() throws Exception {
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void addMembersWithProductAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void removeMembersWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void addMembersWithStaffAuthority() throws Exception {
        // User IDs are required.
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.emptyList()).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(null)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // OK
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        // No one to add.
        mvc.perform(post("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails("admin")
    public void removeMembersWithStaffAuthority() throws Exception {
        // OK
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        // No one to remove.
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(null)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(delete("/api/organizations/{organizationId}/members", "101")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails
    public void addMembersWithNonExistingOrganization() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/members", "200")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberPostVO.builder().userIds(Collections.singletonList(5L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails
    public void removeMembersWithNonExistingOrganization() throws Exception {
        mvc.perform(delete("/api/organizations/{organizationId}/members", "200")
                .content(new ObjectMapper().writeValueAsString(OrganizationMemberDeleteVO.builder().userIds(Collections.singletonList(4L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getProductsWithoutAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/products", "100")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getProductsWithUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/products", "100")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test1")
    public void getProductsWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/products", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getProductsWithProductAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/products", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getProductsOfNonExistingOrganization() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/products", "200")).andExpect(status().isNotFound());
    }

    @Test
    public void addProductWithoutAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void addProductWithNonStaffAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void addProductWithStaffAuthority() throws Exception {
        // Product name is required.
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name(null).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("test1")
    public void addProductWithNonMemberAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void addProductWithProductAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void addProductToNonExistingOrganization() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "200")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getPackagesWithoutAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/packages", "100")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getPackagesWithUserAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/packages", "100")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test1")
    public void getPackagesWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/packages", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getPackagesWithProductAuthority() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/packages", "100")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getPackagesOfNonExistingOrganization() throws Exception {
        mvc.perform(get("/api/organizations/{organizationId}/packages", "200")).andExpect(status().isNotFound());
    }

    @Test
    public void addPackageWithoutAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void addPackageWithNonStaffAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void addPackageWithStaffAuthority() throws Exception {
        // Package name is missing
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Products are missing
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.emptyList()).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Duplicate product Ids are not allowed.
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Some products are not exist.
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 999L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Some products are not owned by this organization.
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 201L, 203L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Arrays.asList(200L, 201L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("test1")
    public void addPackageWithNonMemberAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void addPackageWithProductAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "100")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void addPackageToNonExistingOrganization() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/packages", "200")
                .content(new ObjectMapper().writeValueAsString(PackagePostVO.builder().name("Awesome Package").productIds(Collections.singletonList(200L)).build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

}
