package com.example.jwt0401.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER="Authorization";
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }


    // 토큰의 인증 정보를 SecurityContext 에 저장하는 역할을 수행한다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpRequest);//resolveToken 메서드 실행
        String requestUrl=httpRequest.getRequestURI();
        
        // resolveToken을 통해서 토큰을 받아서 유효성 검증 수행, 정상 토큰일시 SecurityContext 저장
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);//유효성 검증
            SecurityContextHolder.getContext().setAuthentication(authentication);// 저장
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestUrl);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestUrl);
        }
        chain.doFilter(request,response);
    }
    
    // Request Header 에서 토큰 정보를 추출하기 위한 메소드
    private String resolveToken(HttpServletRequest request){
        
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);//헤더 추출
        
        // 결과 값이 존재하고 , Bearer 로 시작하면
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);//7번째 인덱스(공백포함0~6)
        }
        return null;// 조건을 만족못하면
    }
}
