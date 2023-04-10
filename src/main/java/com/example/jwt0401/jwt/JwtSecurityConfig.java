package com.example.jwt0401.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// SecurityConfig 적용 시 사용하기 위해 만든 클래스
// TokenProvider, JwtFilter 적용
// TokenProvider 주입받아서, JwtFilter 를 통해 Security 로직에 필터 등록
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private TokenProvider tokenProvider;
    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider=tokenProvider;
    }
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }

}
