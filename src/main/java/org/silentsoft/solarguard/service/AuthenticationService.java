package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.userdetails.UserDetails;
import org.silentsoft.solarguard.core.userdetails.UserDetailsService;
import org.silentsoft.solarguard.entity.LoginSessionEntity;
import org.silentsoft.solarguard.entity.LoginTokenEntity;
import org.silentsoft.solarguard.exception.LoginTokenNotFoundException;
import org.silentsoft.solarguard.repository.LoginSessionRepository;
import org.silentsoft.solarguard.repository.LoginTokenRepository;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.silentsoft.solarguard.vo.JwtTokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LoginTokenRepository loginTokenRepository;

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    public LoginTokenEntity createLoginToken(String username, String password, String ipAddress, String deviceName) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);
        long userId = userDetails.getUserId();
        String accessToken = jwtTokenUtil.generateLoginAccessToken(userId);
        String refreshToken = jwtTokenUtil.generateLoginRefreshToken(userId);

        LoginTokenEntity loginTokenEntity = new LoginTokenEntity();
        loginTokenEntity.setUser(userDetails.getUserEntity());
        loginTokenEntity.setAccessToken(accessToken);
        loginTokenEntity.setRefreshToken(refreshToken);
        loginTokenEntity.setExpiredAt(Timestamp.from(jwtTokenUtil.getExpirationDateFromToken(refreshToken).toInstant()));
        loginTokenEntity.setCreatedBy(userId);
        loginTokenEntity.setUpdatedBy(userId);
        loginTokenEntity = loginTokenRepository.save(loginTokenEntity);

        LoginSessionEntity loginSessionEntity = new LoginSessionEntity();
        loginSessionEntity.setLoginTokenId(loginTokenEntity.getId());
        loginSessionEntity.setIpAddress(ipAddress);
        loginSessionEntity.setDeviceName(deviceName);
        loginSessionEntity.setCreatedBy(userId);
        loginSessionEntity.setUpdatedBy(userId);
        loginSessionRepository.save(loginSessionEntity);

        return loginTokenEntity;
    }

    public JwtTokenVO refreshAccessToken(String refreshToken) {
        long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        LoginTokenEntity loginTokenEntity = loginTokenRepository.findByUser_IdAndRefreshToken(userId, refreshToken).orElse(null);
        if (loginTokenEntity == null) {
            throw new LoginTokenNotFoundException(String.format("The user '%d' does not have a specified refresh token.", userId));
        }
        String newAccessToken = jwtTokenUtil.generateLoginAccessToken(userId);
        loginTokenEntity.setAccessToken(newAccessToken);
        loginTokenEntity.setUpdatedBy(userId);
        loginTokenRepository.save(loginTokenEntity);

        return new JwtTokenVO(newAccessToken, jwtTokenUtil.getExpiryFromNow(newAccessToken));
    }

    public void deleteLoginToken(String refreshToken) {
        long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        LoginTokenEntity loginTokenEntity = loginTokenRepository.findByUser_IdAndRefreshToken(userId, refreshToken).orElse(null);
        if (loginTokenEntity == null) {
            throw new LoginTokenNotFoundException(String.format("The user '%d' does not have a specified refresh token.", userId));
        }
        loginSessionRepository.deleteById(loginTokenEntity.getId());
        loginTokenRepository.delete(loginTokenEntity);
    }

}
