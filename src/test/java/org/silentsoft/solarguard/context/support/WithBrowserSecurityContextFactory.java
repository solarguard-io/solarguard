package org.silentsoft.solarguard.context.support;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.core.context.ThreadLocalHolder;
import org.silentsoft.solarguard.core.context.ThreadLocalKeys;
import org.silentsoft.solarguard.core.userdetails.UserDetailsService;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Collection;

public class WithBrowserSecurityContextFactory implements WithSecurityContextFactory<WithBrowser> {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithBrowser withBrowser) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        ThreadLocalHolder.initialize();
        ThreadLocalHolder.put(ThreadLocalKeys.TOKEN_TYPE, JwtTokenUtil.TokenType.LOGIN_ACCESS_TOKEN);

        UserDetails userDetails = userDetailsService.loadUserByUsername(withBrowser.value());
        Collection<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(Authority.BROWSER_API));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        securityContext.setAuthentication(usernamePasswordAuthenticationToken);

        return securityContext;
    }

}
