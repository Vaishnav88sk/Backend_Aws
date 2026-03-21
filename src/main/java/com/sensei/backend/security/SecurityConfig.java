package com.sensei.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()

            // ❌ Disable default login & basic auth
            .httpBasic().disable()
            .formLogin().disable()

            // ✅ Allow auth endpoints
            .authorizeHttpRequests(auth -> auth
                    .antMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            )

            // ✅ Add JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}