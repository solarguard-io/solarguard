package org.silentsoft.solarguard.context.support;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.core.context.ThreadLocalHolder;
import org.silentsoft.solarguard.core.context.ThreadLocalKeys;
import org.silentsoft.solarguard.core.userdetails.UserDetailsService;
import org.silentsoft.solarguard.repository.ProductRepository;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class WithProductSecurityContextFactory implements WithSecurityContextFactory<WithProduct> {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public SecurityContext createSecurityContext(WithProduct withProduct) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        productRepository.findById(withProduct.value()).ifPresent(entity -> {
            ThreadLocalHolder.initialize();
            ThreadLocalHolder.put(ThreadLocalKeys.TOKEN_TYPE, JwtTokenUtil.TokenType.PRODUCT_ACCESS_TOKEN);
            ThreadLocalHolder.put(ThreadLocalKeys.PRODUCT_ID, entity.getId());

            UserDetails userDetails = userDetailsService.loadUserById(entity.getCreatedBy());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority(Authority.PRODUCT_API)));
            securityContext.setAuthentication(usernamePasswordAuthenticationToken);
        });
        return securityContext;
    }

}
