package org.silentsoft.solarguard.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ActiveProfiles("dev")
public class JwtTokenUtilTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void generateTest() {
        String loginAccessToken = jwtTokenUtil.generateLoginAccessToken(1);
        Assertions.assertEquals(JwtTokenUtil.TokenType.LOGIN_ACCESS_TOKEN, jwtTokenUtil.getTokenType(loginAccessToken));
        Assertions.assertEquals(1, jwtTokenUtil.getUserIdFromToken(loginAccessToken));
        Assertions.assertNull(jwtTokenUtil.getProductIdFromToken(loginAccessToken));

        String loginRefreshToken = jwtTokenUtil.generateLoginRefreshToken(1);
        Assertions.assertEquals(JwtTokenUtil.TokenType.LOGIN_REFRESH_TOKEN, jwtTokenUtil.getTokenType(loginRefreshToken));
        Assertions.assertEquals(1, jwtTokenUtil.getUserIdFromToken(loginRefreshToken));
        Assertions.assertNull(jwtTokenUtil.getProductIdFromToken(loginRefreshToken));

        String personalAccessToken = jwtTokenUtil.generatePersonalAccessToken(1, TimeUnit.DAYS.toMillis(1));
        Assertions.assertEquals(JwtTokenUtil.TokenType.PERSONAL_ACCESS_TOKEN, jwtTokenUtil.getTokenType(personalAccessToken));
        Assertions.assertEquals(1, jwtTokenUtil.getUserIdFromToken(personalAccessToken));
        Assertions.assertNull(jwtTokenUtil.getProductIdFromToken(personalAccessToken));

        String productAccessToken = jwtTokenUtil.generateProductAccessToken(200);
        Assertions.assertEquals(JwtTokenUtil.TokenType.PRODUCT_ACCESS_TOKEN, jwtTokenUtil.getTokenType(productAccessToken));
        Assertions.assertNull(jwtTokenUtil.getUserIdFromToken(productAccessToken));
        Assertions.assertEquals(200, jwtTokenUtil.getProductIdFromToken(productAccessToken));
    }

    @Test
    public void expirationTest() {
        String notExpiredToken = jwtTokenUtil.generatePersonalAccessToken(1, TimeUnit.DAYS.toMillis(1));
        Assertions.assertTrue(jwtTokenUtil.notExpiredToken(notExpiredToken));

        String expiredToken = jwtTokenUtil.generatePersonalAccessToken(1, 0);
        Assertions.assertTrue(jwtTokenUtil.expiredToken(expiredToken));
    }

    @Test
    public void identifierTest() {
        Assertions.assertNull(JwtTokenUtil.TokenType.fromIdentifier(0));
        Assertions.assertEquals(JwtTokenUtil.TokenType.LOGIN_ACCESS_TOKEN, JwtTokenUtil.TokenType.fromIdentifier(1));
        Assertions.assertEquals(JwtTokenUtil.TokenType.LOGIN_REFRESH_TOKEN, JwtTokenUtil.TokenType.fromIdentifier(2));
        Assertions.assertEquals(JwtTokenUtil.TokenType.PERSONAL_ACCESS_TOKEN, JwtTokenUtil.TokenType.fromIdentifier(3));
        Assertions.assertEquals(JwtTokenUtil.TokenType.PRODUCT_ACCESS_TOKEN, JwtTokenUtil.TokenType.fromIdentifier(4));

    }

}
