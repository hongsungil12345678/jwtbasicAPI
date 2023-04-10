package com.example.jwt0401.config;

import com.example.jwt0401.jwt.JwtAccessDeniedHandler;
import com.example.jwt0401.jwt.JwtAuthenticationEntryPoint;
import com.example.jwt0401.jwt.JwtSecurityConfig;
import com.example.jwt0401.jwt.TokenProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

// @PreAuthorize 메소드 단위로 추가하기 위해서 적용
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {


    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;
    public SecurityConfig(TokenProvider tokenProvider,CorsFilter corsFilter ,JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,JwtAccessDeniedHandler jwtAccessDeniedHandler){
        this.tokenProvider = tokenProvider;
        this.corsFilter=corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler= jwtAccessDeniedHandler;
    }

    // PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){// 필터 등록X
//        return (web)->web
//                .ignoring()
//                .antMatchers("/h2-console/**","/favicon.ico");
//    }
    /// "/api/test" 빼고는 인증이 필요하다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
        http
                .csrf().disable()//토큰을 사용하기 때문에 csrf설정 disable
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()//Excption Handling시 작성한 클래스 등록
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()//h2-console 설정
                .headers()
                .frameOptions()
                .sameOrigin()
                
                .and()// 세션을 사용하지 않기때문에 STATELESS 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()//로그인, 회원가입은 토큰이 없을때 발생하므로
                .antMatchers("/api/authenticated").permitAll()
                .antMatchers("/api/signup").permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .anyRequest().authenticated()

                // JwtFilter를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }



}



//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/api/test").permitAll()
//                .anyRequest()
//                .authenticated();
//        return http.build();
//    }