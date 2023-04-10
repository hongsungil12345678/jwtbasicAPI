package com.example.jwt0401.repository;

import com.example.jwt0401.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long> {
    @EntityGraph(attributePaths = "authorities")
    // "JPA DATA jpql  select * ~~~~ 그거 권한에xxx 추가 내용은 추후 정리할 것
    Optional<Users> findOneWithAuthoritiesByUsername(String username);
}
