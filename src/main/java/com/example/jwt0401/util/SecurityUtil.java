package com.example.jwt0401.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

// getCurrentUsername ->  Security Context의 Authentication 객체를 이용해 username return
public class SecurityUtil {

    private static final Logger logger= LoggerFactory.getLogger(SecurityUtil.class);
    private SecurityUtil() {
    }

    // Security Context에  Authentication 객체가 저장되는 시점 -> JwtFilter의 doFilter()메소드에 Request 들어올때.
    // Security Context에 Authentication 객체를 저장해서 사용하게 된다.
    public static Optional<String> getCurrentUsername(){
        final Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        
        if(authentication == null){
            logger.debug("Security Context에 인증정보가 없음");
            return Optional.empty();
        }
        String username=null;
        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails securityUser=(UserDetails) authentication.getPrincipal();
            username=securityUser.getUsername(); // 인증정보를 통해서 username 저장
        }else if(authentication.getPrincipal() instanceof String){
            username=(String) authentication.getPrincipal(); // 없을 경우 username 저장
        }
        return Optional.ofNullable(username); // username return
    }
}
