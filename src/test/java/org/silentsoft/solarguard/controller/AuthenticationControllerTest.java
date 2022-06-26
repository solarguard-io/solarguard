package org.silentsoft.solarguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.vo.JwtTokenVO;
import org.silentsoft.solarguard.vo.LoginVO;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void loginAndRefreshTokenAndLogoutTest() throws Exception {
        mvc.perform(post("/authentication/token")
                .content(new ObjectMapper().writeValueAsString(new LoginVO("user", "user", "device")))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isCreated()
        ).andExpect(
                cookie().exists("refreshToken")
        ).andDo(tokenResult -> {
            String refreshToken = tokenResult.getResponse().getCookie("refreshToken").getValue();
            String accessToken = new ObjectMapper().readValue(tokenResult.getResponse().getContentAsString(), JwtTokenVO.class).getAccessToken();

            mvc.perform(patch("/authentication/token")
                    .cookie(new Cookie("refreshToken", refreshToken))
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.BEARER_PREFIX.concat(accessToken))
            ).andExpect(
                    status().isOk()
            ).andDo(refreshResult -> {
                String newAccessToken = new ObjectMapper().readValue(refreshResult.getResponse().getContentAsString(), JwtTokenVO.class).getAccessToken();
                Assertions.assertNotEquals(accessToken, newAccessToken);

                mvc.perform(delete("/authentication/token")
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.BEARER_PREFIX.concat(newAccessToken))
                ).andExpect(
                        status().isNoContent()
                ).andExpect(
                        cookie().maxAge("refreshToken", 0)
                );
            });
        });
    }

    @Test
    public void loginWithInvalidUserInfoTest() throws Exception {
        mvc.perform(post("/authentication/token")
                .content(new ObjectMapper().writeValueAsString(new LoginVO("invalid", "invalid", "device")))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    @WithUserDetails
    public void refreshAccessTokenWithInvalidRefreshTokenTest() throws Exception {
        mvc.perform(patch("/authentication/token")
                .cookie(new Cookie("refreshToken", "invalid"))
        ).andExpect(
                status().isBadRequest()
        );

        mvc.perform(patch("/authentication/token")
                .cookie(new Cookie("refreshToken", jwtTokenUtil.generateLoginRefreshToken(1)))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    @WithUserDetails
    public void logoutWithInvalidRefreshTokenTest() throws Exception {
        mvc.perform(delete("/authentication/token")
                .cookie(new Cookie("refreshToken", "invalid"))
        ).andExpect(
                status().isBadRequest()
        );

        mvc.perform(delete("/authentication/token")
                .cookie(new Cookie("refreshToken", jwtTokenUtil.generateLoginRefreshToken(1)))
        ).andExpect(
                status().isBadRequest()
        );
    }

}
