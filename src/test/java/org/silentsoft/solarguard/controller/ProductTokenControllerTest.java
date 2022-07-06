package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.vo.ProductTokenPatchVO;
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
public class ProductTokenControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getProductTokenWithoutAuthority() throws Exception {
        mvc.perform(get("/api/product-tokens/600")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void getProductTokenWithMemberAuthority() throws Exception {
        mvc.perform(get("/api/product-tokens/600")).andExpect(status().isOk());
    }

    @Test
    @WithBrowser("test3")
    public void getProductTokenWithNonMemberAuthority() throws Exception {
        mvc.perform(get("/api/product-tokens/600")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getProductTokenWithProductAuthority() throws Exception {
        mvc.perform(get("/api/product-tokens/600")).andExpect(status().isForbidden());
    }

    @Test
    public void patchProductTokenWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/product-tokens/600")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().note("test").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void patchProductTokenWithMemberAuthority() throws Exception {
        mvc.perform(patch("/api/product-tokens/600")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().note("test").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("test3")
    public void patchProductTokenWithNonMemberAuthority() throws Exception {
        mvc.perform(patch("/api/product-tokens/600")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().note("test").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void patchProductTokenWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/product-tokens/600")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().note("test").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void patchProductTokenWithStaffAuthority() throws Exception {
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token BBB").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andDo(result -> {
            ProductTokenEntity productToken = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProductTokenEntity.class);
            mvc.perform(patch("/api/product-tokens/" + productToken.getId())
                    .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().note("test").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
            mvc.perform(patch("/api/product-tokens/" + productToken.getId())
                    .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().revoke(true).build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
            mvc.perform(patch("/api/product-tokens/" + productToken.getId())
                    .content(new ObjectMapper().writeValueAsString(ProductTokenPatchVO.builder().revoke(false).build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        });
    }

    @Test
    public void deleteProductTokenWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/product-tokens/600")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBrowser
    public void deleteProductTokenWithMemberAuthority() throws Exception {
        mvc.perform(delete("/api/product-tokens/600")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("test3")
    public void deleteProductTokenWithNonMemberAuthority() throws Exception {
        mvc.perform(delete("/api/product-tokens/600")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void deleteProductTokenWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/product-tokens/600")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser("admin")
    public void deleteProductTokenWithStaffAuthority() throws Exception {
        mvc.perform(post("/api/products/200/tokens")
                .content(new ObjectMapper().writeValueAsString(ProductTokenPostVO.builder().note("Product Token CCC").build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andDo(result -> {
            ProductTokenEntity productToken = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProductTokenEntity.class);
            mvc.perform(delete("/api/product-tokens/" + productToken.getId())).andExpect(status().isNoContent());
        });
    }

}
