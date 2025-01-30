package com.coin.now_coin.common.jwt;


import com.coin.now_coin.member.dto.CreateMemberJwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;


    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;


    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.token.expiration.time}") long accessTokenExpirationTime,
            @Value("${jwt.refresh.token.expiration.time}") long refreshTokenExpirationTime
    ) {
        //Encoding in HS256
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpirationTime = accessTokenExpirationTime * 1000; // 초 단위를 밀리초 단위로 변환
        this.refreshTokenExpirationTime = refreshTokenExpirationTime * 1000; // 초 단위를 밀리초 단위로 변환
    }


    public String generateAccessToken(CreateMemberJwtDto memberJwtCreateDto) {
        return Jwts.builder()
                .claim("name", memberJwtCreateDto.getName())//닉네임 생성
                .claim("role", memberJwtCreateDto.getMemberRole())//역할 생성
                .claim("providerId", memberJwtCreateDto.getProviderId())//providerId 저장
                .issuedAt(new Date(System.currentTimeMillis()))//JWT creation time
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))//JWT expiration time
                .signWith(secretKey)
                .compact();
    }

    //리프레시 토큰 발급 메서드
    public String generateRefreshToken(String providerId,String name) {

        return Jwts.builder()
                .claim("providerId", providerId)
                .claim("name",name)
                .issuedAt(new Date(System.currentTimeMillis()))//JWT creation time
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))//JWT expiration time
                .signWith(secretKey)
                .compact();

    }

    public JwtStatus validateJwt(String jwt) {
        try {
            if (jwt == null || jwt.isBlank()) {
                log.warn("JWT가 비어있음");
                return JwtStatus.INVALID;
            }

            if (!jwt.contains(".")) { // JWT 형식 검사 ('.' 포함 여부 확인)
                log.warn("잘못된 JWT 형식: {}", jwt);
                return JwtStatus.INVALID;
            }

            Claims body = Jwts.parser()
                    .verifyWith(secretKey) // JWT key 검증
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            Date expiration = body.getExpiration();
            if(expiration == null || !expiration.before(new Date(System.currentTimeMillis()))){

                return JwtStatus.ISSUED;//정상적으로 발급된경우

            }else {
                return JwtStatus.EXPIRED;//기간만료
            }


        } catch (MalformedJwtException ex) {
            log.error("잘못된 JWT 형식: {}", jwt);
        } catch (ExpiredJwtException ex) {
            log.error("JWT 만료됨: {}", jwt);
            return JwtStatus.EXPIRED;
        } catch (Exception ex) {
            log.error("validateJwt 실패, JWT ={}", jwt, ex);
        }

        return JwtStatus.INVALID;
    }


    public boolean validateRefreshToken(String refreshToken) {


        return true;

    }

    public Claims getClaims(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

        }
        catch (ExpiredJwtException e) {
            // 만료된 토큰의 클레임을 반환
            return e.getClaims();
        } catch (Exception ex) {
            log.error("Invalid JWT token", ex);
            return null;
        }
    }

    public String getClaimValue(String jwt, String claimKey) {
        Claims claims = getClaims(jwt);
        if (claims != null) {
            return claims.get(claimKey, String.class); // 원하는 클레임 값을 추출
        }
        return null;
    }

    public long getExpirationTime() {
        return accessTokenExpirationTime / 1000;//초단위로 변경하여 return

    }


}
