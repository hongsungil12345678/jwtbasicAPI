package com.example.jwt0401.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userid;

    @Column(length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(length = 100)
    private String password;

    @Column(length = 50)
    private String nickname;

    @JsonIgnore
    @Column()
    private boolean activated;

    // M : M 매핑
    @ManyToMany
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name="user_id",referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name="authority_name",referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;

}
