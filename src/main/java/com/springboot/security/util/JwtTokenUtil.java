package com.springboot.security.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

//토큰 발급, 자격증명 관리
public class JwtTokenUtil {
    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName); //claim : token에 담는 정보. key/value쌍으로 저장

        return Jwts.builder()
                .setClaims(claims) //claims 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) //현재 시간으로 토큰 발급시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) //토큰 만료시간 설정
                .signWith(SignatureAlgorithm.HS256, key) //사용할 알고리즘 설정
                .compact(); //토큰 생성
    }
}
