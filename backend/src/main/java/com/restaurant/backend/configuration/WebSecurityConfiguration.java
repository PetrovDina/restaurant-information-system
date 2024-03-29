package com.restaurant.backend.configuration;

import javax.annotation.PostConstruct;

import com.restaurant.backend.security.TokenUtils;
import com.restaurant.backend.security.authentication.RestAuthenticationEntryPoint;
import com.restaurant.backend.security.authentication.TokenAuthenticationFilter;
import com.restaurant.backend.security.authentication.provider.PinUserAuthenticationProvider;
import com.restaurant.backend.service.JWTUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    private TokenUtils tokenUtils;
    private JWTUserDetailsService jwtUserDetailsService;
    private PinUserAuthenticationProvider pinUserAuthenticationProvider;
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @PostConstruct
    public void init() throws Exception {
        jwtUserDetailsService.setPasswordEncoder(passwordEncoder());
        jwtUserDetailsService.setAuthenticationManager(authenticationManagerBean());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(pinUserAuthenticationProvider);
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // No session will be created or used by spring security
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and().authorizeRequests()
                // Keep authentication open but close all other requests.
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/table").permitAll()  // for testing
                .antMatchers("/api/item/**").permitAll()  // for testing
                .antMatchers("/api/categories/**").permitAll()  // for testing
                .antMatchers("/api/websocket").permitAll()  // for testing
                .antMatchers("/api/websocket/**").permitAll()  // for testing
                .antMatchers("/api/h2-console/**").permitAll().anyRequest()
                .authenticated().and().cors().and()
                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, jwtUserDetailsService),
                        BasicAuthenticationFilter.class)
                .addFilterBefore(new FrontendRedirectFilter(), TokenAuthenticationFilter.class);

        http.csrf().disable(); // disable cross site request forgery, as we don't use cookies - otherwise ALL
                               // PUT, POST, DELETE will get HTTP 403
        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        // Ignore all requests to endpoints that do not contain /api/
        web.ignoring().regexMatchers(HttpMethod.GET, "/((?!api).*)");
    }
}
