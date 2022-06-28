package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.entity.UserRole;
import org.silentsoft.solarguard.vo.UserPatchVO;
import org.silentsoft.solarguard.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getUsersWithoutAuthority() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserWithoutAuthority() throws Exception {
        mvc.perform(get("/api/users/1")).andExpect(status().isUnauthorized());
    }

    @Test
    public void createUserWithoutAuthority() throws Exception {
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("createUserWithoutAuthority")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void patchUserWithoutAuthority() throws Exception {
        mvc.perform(patch("/api/users/1")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                        .username("patchUserWithoutAuthority")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserWithoutAuthority() throws Exception {
        mvc.perform(delete("/api/users/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    public void getUsersWithUserAuthority() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails
    public void getUserWithUserAuthority() throws Exception {
        mvc.perform(get("/api/users/0")).andExpect(status().isNotFound());
        mvc.perform(get("/api/users/1")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails
    public void createUserWithUserAuthority() throws Exception {
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("createUserWithUserAuthority")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void patchUserWithUserAuthority() throws Exception {
        // not me
        mvc.perform(patch("/api/users/1")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

        // for me
        mvc.perform(patch("/api/users/2")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // users can't change themselves to admin
        mvc.perform(patch("/api/users/2")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                        .role(UserRole.ADMIN)
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void deleteUserWithUserAuthority() throws Exception {
        mvc.perform(delete("/api/users/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    public void getUsersWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    public void getUserWithAdminAuthority() throws Exception {
        mvc.perform(get("/api/users/1")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    public void createUserWithAdminAuthorityTest() throws Exception {
        // Username already exists
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("admin")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Email already exists
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("admin2")
                        .email("admin@solarguard.io")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Username is missing
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        // Password is too short
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("john")
                        .password("doe")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("john")
                        .password("difficult")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        // temporary password: true
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("jane")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("admin")
    public void patchUserWithAdminAuthority() throws Exception {
        mvc.perform(patch("/api/users/0")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        mvc.perform(patch("/api/users/1")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mvc.perform(patch("/api/users/2")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("patchUserWithAdminAuthority")
                        .password("difficult")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(result -> {
            UserEntity createdUser = new ObjectMapper().readValue(result.getResponse().getContentAsString(), UserEntity.class);

            // Username already exists
            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .username("admin")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnprocessableEntity());

            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .username("patchUserWithAdminAuthorityResult")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // Email already exists
            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .email("admin@solarguard.io")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnprocessableEntity());

            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .email("patchUserWithAdminAuthorityResult@solarguard.io")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // Password is too short
            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .password("doe")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnprocessableEntity());

            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .password("super-difficult")
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            mvc.perform(patch("/api/users/" + createdUser.getId())
                    .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder()
                            .role(UserRole.ADMIN)
                            .build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        });
    }

    @Test
    @WithUserDetails("admin")
    public void deleteUserWithAdminAuthority() throws Exception {
        mvc.perform(delete("/api/users/0")).andExpect(status().isNotFound());

        // cannot delete myself
        mvc.perform(delete("/api/users/1")).andExpect(status().isUnprocessableEntity());

        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("deleteUserWithAdminAuthority")
                        .password("difficult")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(result -> {
            UserEntity createdUser = new ObjectMapper().readValue(result.getResponse().getContentAsString(), UserEntity.class);
            mvc.perform(delete("/api/users/" + createdUser.getId())).andExpect(status().isNoContent());

            // already deleted
            mvc.perform(delete("/api/users/" + createdUser.getId())).andExpect(status().isUnprocessableEntity());
        });
    }

    @Test
    @WithProduct(200)
    public void getUsersWithProductAuthority() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void getUserWithProductAuthority() throws Exception {
        mvc.perform(get("/api/users/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void createUserWithProductAuthority() throws Exception {
        mvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(UserPostVO.builder()
                        .username("createUserWithProductAuthority")
                        .password("password")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void patchUserWithProductAuthority() throws Exception {
        mvc.perform(patch("/api/users/1")
                .content(new ObjectMapper().writeValueAsString(UserPatchVO.builder().build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithProduct(200)
    public void deleteUserWithProductAuthority() throws Exception {
        mvc.perform(delete("/api/users/1")).andExpect(status().isForbidden());
    }

}
