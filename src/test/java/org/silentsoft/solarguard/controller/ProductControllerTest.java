package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.vo.ProductPatchVO;
import org.silentsoft.solarguard.vo.ProductPostVO;
import org.silentsoft.solarguard.vo.ProductTokenPostVO;
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
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getProductsWithoutAuthority() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void getProductsWithUserAuthority() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getProductsWithProductAuthority() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void getProductsWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    public void getProductWithoutAuthority() throws Exception {
        mvc.perform(get("/api/products/200")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void getProductWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/products/200")).andExpect(status().isOk());
    }

    @Test
    @WithBrowser("test3")
    public void getProductWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/products/200")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getProductWithProductAuthority() throws Exception {
        mvc.perform(get("/api/products/200")).andExpect(status().isForbidden());
    }

    @Test
    public void patchProductWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("Product A2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void patchProductWithMemberAuthority() throws Exception {
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("Product A2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("test3")
    public void patchProductWithNonMemberAuthority() throws Exception {
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("Product A2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void patchProductWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("Product A2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void patchProductWithStaffAuthority() throws Exception {
        // Name is missing.
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(patch("/api/products/200")
                .content(new ObjectMapper().writeValueAsString(ProductPatchVO.builder().name("Product A2").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithBrowser
    public void deleteProductWithMemberAuthority() throws Exception {
        mvc.perform(delete("/api/products/200")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("test3")
    public void deleteProductWithNonMemberAuthority() throws Exception {
        mvc.perform(delete("/api/products/200")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void deleteProductWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/products/200")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void deleteProductWithStaffAuthority() throws Exception {
        mvc.perform(post("/api/organizations/{organizationId}/products", "100")
                .content(new ObjectMapper().writeValueAsString(ProductPostVO.builder().name("Super Cool Product").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andDo(result -> {
            ProductEntity product = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProductEntity.class);
            mvc.perform(delete("/api/products/" + product.getId())).andExpect(status().isNoContent());
        });
    }

    @Test
    public void getProductTokensWithoutAuthority() throws Exception {
        mvc.perform(get("/api/products/200/tokens")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void getProductTokensWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/products/200/tokens")).andExpect(status().isOk());
    }

    @Test
    @WithBrowser("test3")
    public void getProductTokensWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/products/200/tokens")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getProductTokensWithProductAuthority() throws Exception {
        mvc.perform(get("/api/products/200/tokens")).andExpect(status().isForbidden());
    }

    @Test
    public void createProductTokenWithoutAuthority() throws Exception {
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token AAA").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void createProductTokenWithProductAuthority() throws Exception {
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token AAA").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser
    public void createProductTokenWithMemberAuthority() throws Exception {
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token AAA").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void createProductTokenWithStaffAuthority() throws Exception {
        // Product note is missing.
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token AAA").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

}
