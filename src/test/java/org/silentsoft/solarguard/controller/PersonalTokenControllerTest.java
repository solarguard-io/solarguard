package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.vo.PersonalTokenPatchVO;
import org.silentsoft.solarguard.vo.PersonalTokenPostVO;
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
public class PersonalTokenControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getPersonalTokenWithoutAuthority() throws Exception {
        mvc.perform(get("/api/personal-tokens/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithProduct(200)
    public void getPersonalTokenWithProductAuthority() throws Exception {
        mvc.perform(get("/api/personal-tokens/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    public void getPersonalTokenWithoutBrowser() throws Exception {
        mvc.perform(get("/api/personal-tokens/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithBrowser
    public void getPersonalTokenWithBrowser() throws Exception {
        mvc.perform(get("/api/personal-tokens/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithBrowser
    public void personalTokenWithBrowserTest() throws Exception {
        mvc.perform(post("/api/users/2/tokens")
                .content(new ObjectMapper().writeValueAsString(PersonalTokenPostVO.builder()
                        .note("personalTokenWithBrowserTest")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()
        ).andDo(result -> {
            PersonalTokenEntity createdPersonalToken = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PersonalTokenEntity.class);

            mvc.perform(get("/api/users/2/tokens")).andExpect(status().isOk()).andDo(result2 -> {
                PersonalTokenEntity[] personalTokens = new ObjectMapper().readValue(result2.getResponse().getContentAsString(), PersonalTokenEntity[].class);
                Assertions.assertTrue(personalTokens.length > 0);
                boolean found = false;
                for (PersonalTokenEntity personalToken : personalTokens) {
                    if (personalToken.getId().equals(createdPersonalToken.getId())) {
                        Assertions.assertEquals(personalToken.getNote(), createdPersonalToken.getNote());
                        found = true;
                    }
                }
                Assertions.assertTrue(found);
            });

            mvc.perform(get("/api/personal-tokens/" + createdPersonalToken.getId()))
                    .andExpect(status().isOk())
                    .andDo(result2 -> {
                        PersonalTokenEntity personalToken = new ObjectMapper().readValue(result2.getResponse().getContentAsString(), PersonalTokenEntity.class);
                        Assertions.assertEquals(createdPersonalToken.getId(), personalToken.getId());
                        Assertions.assertEquals(createdPersonalToken.getNote(), personalToken.getNote());
                    });
            mvc.perform(patch("/api/personal-tokens/" + createdPersonalToken.getId())
                    .content(new ObjectMapper().writeValueAsString(PersonalTokenPatchVO.builder().note("personalTokenWithBrowserTest_updated").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk()
            ).andDo(result2 -> {
                PersonalTokenEntity personalToken = new ObjectMapper().readValue(result2.getResponse().getContentAsString(), PersonalTokenEntity.class);
                Assertions.assertEquals(createdPersonalToken.getId(), personalToken.getId());
                Assertions.assertEquals("personalTokenWithBrowserTest_updated", personalToken.getNote());
            });
            mvc.perform(delete("/api/personal-tokens/" + createdPersonalToken.getId())).andExpect(status().isNoContent());
            mvc.perform(get("/api/personal-tokens/" + createdPersonalToken.getId())).andExpect(status().isNotFound());

            mvc.perform(get("/api/users/2/tokens")).andExpect(status().isOk()).andDo(result2 -> {
                PersonalTokenEntity[] personalTokens = new ObjectMapper().readValue(result2.getResponse().getContentAsString(), PersonalTokenEntity[].class);
                boolean found = false;
                for (PersonalTokenEntity personalToken : personalTokens) {
                    if (personalToken.getId().equals(createdPersonalToken.getId())) {
                        found = true;
                    }
                }
                Assertions.assertFalse(found);
            });
        });
    }

}
