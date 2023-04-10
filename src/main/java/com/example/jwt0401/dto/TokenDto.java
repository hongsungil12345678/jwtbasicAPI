package com.example.jwt0401.dto;

import lombok.*;

// Token 정보를 Response할때 사용
@Getter@Setter@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String token;
}
