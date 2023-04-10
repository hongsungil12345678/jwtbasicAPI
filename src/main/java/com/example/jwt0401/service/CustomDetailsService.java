package com.example.jwt0401.service;

import com.example.jwt0401.entity.Users;
import com.example.jwt0401.repository.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    public CustomDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }
    // loadUserByUsername -> 로그인 시 데이터 베이스에서 유저 정보와 권한 정보를 가져오게 된다.
    // -> 해당 정보를 기반으로  userdetails , User 객체를 생성해서 return
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username){
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(users -> createUser(username,users))
                .orElseThrow(() -> new UsernameNotFoundException(username+"해당 결과가 없습니다."));
    }
    private org.springframework.security.core.userdetails.User createUser(String username,Users user){
        if(!user.isActivated()){
            throw new RuntimeException(username+"가 활성화 되어 있지 않습니다.");
        }
        // 권한 정보- 객체(Users) 생성
        List<GrantedAuthority> grantedAuthorities= user.getAuthorities()
                .stream()
                .map(auth-> new SimpleGrantedAuthority(auth.getAuthorityName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),grantedAuthorities);
    }
}
