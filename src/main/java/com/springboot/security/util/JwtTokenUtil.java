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

    //token에서 secretKey 받아와 인증
    private static Claims extractClaims(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //token 내 Claim에 사용자 정보가 담겨있는지 확인하기 위해 UserName 추출
    public static String getUserName(String token, String secretKey) {
        return extractClaims(token, secretKey).get("userName", String.class);
    }

    //token이 만료되면 접근 거부하기
    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

}
