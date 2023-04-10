package com.example.jwt0401.controller;


// TokenProvider , AuthenticationMangerBuilder 주입.

import com.example.jwt0401.dto.LoginDto;
import com.example.jwt0401.dto.TokenDto;
import com.example.jwt0401.jwt.JwtFilter;
import com.example.jwt0401.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController{
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider=tokenProvider;
        this.authenticationManagerBuilder=authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    // 로그인 Dto를 통해서 데이터 받고 TokenDto를 통해서 토큰 반환
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto){
        // 토큰 객체 생성
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword());

        // 발급받은 토큰 authenticate
        // usernamePasswordAuthenticationToken 를 통해서 authenticationManagerBuilder.getObject().authenticate() 실행시
        // loadUserByUsername() 이 실행된다.
        // 결과값을 통해서 Authentication 객체 생성이 된다.
        Authentication authentication= authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);

        //생성된 Authentication 객체가 생성 된 것을 Security Context Holder에 저장하고
        // Authentication 객체를 createToken()를 통해서 JWT 토큰을 생성한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);// SecurityContext저장

        String jwt = tokenProvider.createToken(authentication);// jwt 토큰 생성

        // 생성된 JWT 토큰을 헤더에 넣어준다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,"Bearer "+jwt);

        //결과 값 token Dto를 통해서 리턴
        return new ResponseEntity<>(new TokenDto(jwt),httpHeaders, HttpStatus.OK);
    }

}
