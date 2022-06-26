package org.silentsoft.solarguard.core.filter;

import io.jsonwebtoken.lang.Strings;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.core.context.ThreadLocalHolder;
import org.silentsoft.solarguard.core.context.ThreadLocalKeys;
import org.silentsoft.solarguard.core.userdetails.UserDetailsService;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.repository.LoginSessionRepository;
import org.silentsoft.solarguard.repository.LoginTokenRepository;
import org.silentsoft.solarguard.repository.PersonalTokenRepository;
import org.silentsoft.solarguard.repository.ProductTokenRepository;
import org.silentsoft.solarguard.service.PersonalTokenStatisticsService;
import org.silentsoft.solarguard.service.ProductTokenStatisticsService;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LoginTokenRepository loginTokenRepository;

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Autowired
    private PersonalTokenRepository personalTokenRepository;

    @Autowired
    private PersonalTokenStatisticsService personalTokenStatisticsService;

    @Autowired
    private ProductTokenRepository productTokenRepository;

    @Autowired
    private ProductTokenStatisticsService productTokenStatisticsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String jwtToken = jwtTokenUtil.removeBearerPrefix(request.getHeader(HttpHeaders.AUTHORIZATION));
            if (Strings.hasLength(jwtToken) && Strings.countOccurrencesOf(jwtToken, ".") == 2) {
                if (jwtTokenUtil.expiredToken(jwtToken)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                } else {
                    try {
                        JwtTokenUtil.TokenType tokenType = jwtTokenUtil.getTokenType(jwtToken);
                        ThreadLocalHolder.initialize();
                        ThreadLocalHolder.put(ThreadLocalKeys.TOKEN_TYPE, tokenType);
                        switch (tokenType) {
                            case LOGIN_ACCESS_TOKEN:
                            case PERSONAL_ACCESS_TOKEN:
                                Long userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
                                if (userId == null) {
                                    response.sendError(HttpStatus.PRECONDITION_REQUIRED.value());
                                } else {
                                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                                    if (userDetails != null && userDetails.isAccountNonLocked()) {
                                        boolean shouldAuthenticate = false;
                                        Collection<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
                                        if (JwtTokenUtil.TokenType.PERSONAL_ACCESS_TOKEN.equals(tokenType)) {
                                            PersonalTokenEntity entity = personalTokenRepository.findByUserIdAndAccessToken(userId, jwtToken).orElse(null);
                                            if (entity == null) {
                                                response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value());
                                            } else if (Boolean.TRUE.equals(entity.getIsRevoked())) {
                                                response.sendError(HttpStatus.UNAUTHORIZED.value());
                                            } else {
                                                ThreadLocalHolder.put(ThreadLocalKeys.PERSONAL_TOKEN_ID, entity.getId());

                                                authorities.add(new SimpleGrantedAuthority(Authority.PERSONAL_API));

                                                shouldAuthenticate = true;
                                            }
                                        } else if (JwtTokenUtil.TokenType.LOGIN_ACCESS_TOKEN.equals(tokenType)) {
                                            loginTokenRepository.findByUser_IdAndAccessToken(userId, jwtToken).ifPresent(loginTokenEntity -> {
                                                loginSessionRepository.findById(loginTokenEntity.getId()).ifPresent(loginSessionEntity -> {
                                                    loginSessionEntity.setUsedAt(new Timestamp(System.currentTimeMillis()));
                                                    loginSessionRepository.save(loginSessionEntity);
                                                });
                                            });

                                            authorities.add(new SimpleGrantedAuthority(Authority.BROWSER_API));

                                            shouldAuthenticate = true;
                                        }
                                        if (shouldAuthenticate) {
                                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                                            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                        }
                                    } else {
                                        response.sendError(HttpStatus.UNAUTHORIZED.value());
                                    }
                                }
                                break;
                            case PRODUCT_ACCESS_TOKEN:
                                Long productId = jwtTokenUtil.getProductIdFromToken(jwtToken);
                                if (productId == null) {
                                    response.sendError(HttpStatus.PRECONDITION_REQUIRED.value());
                                } else {
                                    ProductTokenEntity entity = productTokenRepository.findByProductIdAndAccessToken(productId, jwtToken);
                                    if (entity == null) {
                                        response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value());
                                    } else if (Boolean.TRUE.equals(entity.getIsRevoked())) {
                                        response.sendError(HttpStatus.UNAUTHORIZED.value());
                                    } else {
                                        ThreadLocalHolder.put(ThreadLocalKeys.PRODUCT_ID, productId);
                                        ThreadLocalHolder.put(ThreadLocalKeys.PRODUCT_TOKEN_ID, entity.getId());

                                        UserDetails userDetails = userDetailsService.loadUserById(entity.getCreatedBy());
                                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority(Authority.PRODUCT_API)));
                                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                    }
                                }
                                break;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (ThreadLocalHolder.isInitialized()) {
                if (ThreadLocalHolder.get(ThreadLocalKeys.TOKEN_TYPE) == JwtTokenUtil.TokenType.PRODUCT_ACCESS_TOKEN && ThreadLocalHolder.get(ThreadLocalKeys.PRODUCT_TOKEN_ID) != null) {
                    productTokenStatisticsService.updateProductTokenStatistics((long) ThreadLocalHolder.get(ThreadLocalKeys.PRODUCT_TOKEN_ID), response.getStatus());
                } else if (ThreadLocalHolder.get(ThreadLocalKeys.TOKEN_TYPE) == JwtTokenUtil.TokenType.PERSONAL_ACCESS_TOKEN && ThreadLocalHolder.get(ThreadLocalKeys.PERSONAL_TOKEN_ID) != null) {
                    personalTokenStatisticsService.updatePersonalTokenStatistics((long) ThreadLocalHolder.get(ThreadLocalKeys.PERSONAL_TOKEN_ID), response.getStatus());
                }
            }

            ThreadLocalHolder.destroy();
        }
    }
}
