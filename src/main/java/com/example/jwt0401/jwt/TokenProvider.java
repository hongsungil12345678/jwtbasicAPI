package com.example.jwt0401.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// TokenProvider -> createToken, getAuthentication, validateToken 구현
@Component
public class
TokenProvider implements InitializingBean {

    private static final String AUTHORIZATION_KEY="auth";
    private final String secret;
    private final Long tokenValidationSeconds;
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") Long tokenValidationSeconds){
        this.secret=secret;
        this.tokenValidationSeconds=tokenValidationSeconds*1000;
    }
    // secret 값 주입받은거 decode 해서 Key 객체에 저장
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] decodeByte = Decoders.BASE64.decode(secret);//결과값
        this.key= Keys.hmacShaKeyFor(decodeByte);// 객체 저장
    }

    // createToken : 인증 정보를 기반으로 JWT 토큰 생성
    // Authentication 객체의 권한 정보를 이용해서 토큰을 생성해서 리턴 하는 메소드
    public String createToken(Authentication authentication){
        String authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long nowDate= new Date().getTime();

        Date validity=new Date(nowDate+tokenValidationSeconds);//유효기간 설정

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORIZATION_KEY,authorities)
                .setExpiration(validity)
                .compact();
    }
    // JWT 토큰에 담겨 있는 정보를 이용해서 Authentication 객체를 리턴하는  메소드
    public Authentication getAuthentication(String token) {
        // JWT 토큰에서 인증 정보 추출
        Claims claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities=
                Arrays.stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
    }
    // JWT 토큰 검증
    // 토큰을 파라미터로 받아서 유효성 검사, 토큰을 파싱해보고 발생하는 Exception Catch
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;// 참일경우
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}




