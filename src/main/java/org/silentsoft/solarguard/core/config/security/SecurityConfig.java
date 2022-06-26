package org.silentsoft.solarguard.core.config.security;

import org.silentsoft.solarguard.core.filter.AuthenticationFilter;
import org.silentsoft.solarguard.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Order(1)
    @Configuration
    public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.antMatcher("/api/**")
                    .cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and().exceptionHandling().authenticationEntryPoint(apiAuthenticationEntryPoint)
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    public class NoneApiSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private NoneApiAuthenticationEntryPoint noneApiAuthenticationEntryPoint;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.headers().frameOptions().sameOrigin();

            httpSecurity.cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/static/**").permitAll()
                    .antMatchers("/login", "/logout").permitAll()
                    .antMatchers(HttpMethod.POST, "/authentication/token").permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
                    //.and().csrf().ignoringAntMatchers("/h2-console/**")
                    .and().exceptionHandling().authenticationEntryPoint(noneApiAuthenticationEntryPoint)
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

}