package com.example.demo.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    // secretKey : 객체 키를 저장할 변수섭
    private SecretKey secretKey;

    // 생성자 방식으로 JWTUtil 클래스 호출될 때 application.properties에 미리 저장해둔 시크릿키(spring.jwt.secret) 불러오기
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        //스트링 키 secret을 기반으로 객체 키 secretKey 생성. (객체 타입으로 만들어서 키 저장)
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 검증 메서드 (username 뽑아서 확인)
    public String getUsername(String token) {

        // verifyWith : 토큰 secretKey가 일치하는지 검증 진행
        // 토큰 검증 후 claims 마친 후 스트링 타입의 username을 가져옴
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 검증 메서드 (role 뽑아서 확인)
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 검증 메서드 (토큰 만료 확인)
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 토큰 생성 메서드
    // 토큰이 string이므로 string 타입 리턴
    public String createJwt(String username, String role, Long expiredMs) { //expiredMs : 토큰 유효시간

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발행시간 (혅재시간)
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //토큰 소멸시간 (현재시간+토큰유효시간)
                .signWith(secretKey) //secretKey를 통해 암호화 진행
                .compact(); // 토큰을 compact 시킴
    }
}
