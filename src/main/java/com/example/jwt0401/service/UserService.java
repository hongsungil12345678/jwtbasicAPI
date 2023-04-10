package com.example.jwt0401.service;

import com.example.jwt0401.dto.UserDto;
import com.example.jwt0401.entity.Authority;
import com.example.jwt0401.entity.Users;
import com.example.jwt0401.repository.UsersRepository;
import com.example.jwt0401.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder){
        this.usersRepository=usersRepository;
        this.passwordEncoder=passwordEncoder;
    }
    @Transactional
    public UserDto signup(UserDto userDto){
        if(usersRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null)!=null){
            throw new RuntimeException("이미 가입 되어있습니다.");
        }
        // 권한 주입
        Authority authority=Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        //user 객체 생성
        Users user = Users.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();
        return UserDto.from(usersRepository.save(user));
    }
    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(usersRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(usersRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new IllegalArgumentException("")));
//                        .orElseThrow(() -> new notfound("Member not found"))

    }
}
