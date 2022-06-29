package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.silentsoft.solarguard.entity.LoginTokenEntity;
import org.silentsoft.solarguard.service.AuthenticationService;
import org.silentsoft.solarguard.util.HttpUtil;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.silentsoft.solarguard.vo.JwtTokenVO;
import org.silentsoft.solarguard.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Hidden
@RestController
@CrossOrigin
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createLoginToken(@RequestBody LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginTokenEntity loginTokenEntity = authenticationService.createLoginToken(loginVO.getUsername(), loginVO.getPassword(), HttpUtil.extractIpAddressFromRequest(request), loginVO.getDeviceName());
            setRefreshTokenToCookie(loginTokenEntity.getRefreshToken(), jwtTokenUtil.getExpiryFromNow(loginTokenEntity.getRefreshToken()), response);
            return ResponseEntity.status(HttpStatus.CREATED).body(new JwtTokenVO(loginTokenEntity.getAccessToken(), jwtTokenUtil.getExpiryFromNow(loginTokenEntity.getAccessToken())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken") String refreshToken) {
        try {
            return ResponseEntity.ok(authenticationService.refreshAccessToken(refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/token")
    public ResponseEntity<?> deleteLoginToken(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        try {
            authenticationService.deleteLoginToken(refreshToken);
            deleteRefreshTokenInCookie(response);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void setRefreshTokenToCookie(String refreshToken, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        // TODO cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }

    private void deleteRefreshTokenInCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

}