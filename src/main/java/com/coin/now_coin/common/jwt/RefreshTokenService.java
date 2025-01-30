package com.coin.now_coin.common.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtUtil jwtUtil;

    public RefreshTokenService(RedisTemplate<String, Object> redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Redis 에 리프레시 토큰을 저장하는 메서드
     *
     * @param username
     * @param refreshToken
     */
    public void saveRefreshTokenToRedis(String username, String refreshToken) {
        // 기존 토큰이 있는지 확인
        if (redisTemplate.hasKey(username)) {
            // 기존 토큰이 존재하면 로그 출력 또는 삭제
            String existingToken = (String) redisTemplate.opsForValue().get(username);
            log.warn("Existing refresh token found for user: " + username + ", deleting old token: " + existingToken);
            // 기존 토큰 삭제
            redisTemplate.delete(username);
        }
        //토큰 레디스에 저장
        redisTemplate.opsForValue().set(username, refreshToken, 7, TimeUnit.DAYS); // 7일 TTL 설정
    }

    public JwtStatus validateRefreshToken(String refreshToken) {

        //일단 JWT 유틸에서 JWT 검증하고
        JwtStatus jwtStatus = jwtUtil.validateJwt(refreshToken);
        if (jwtStatus.equals(JwtStatus.INVALID)) {
            log.warn("JWT 토큰 에러임");
            return JwtStatus.INVALID;
        }
        String providerId = jwtUtil.getClaimValue(refreshToken, "providerId");//유저정보


        //Redis랑 값비교하자, 기간이 안지났으면 Redis에 존재함
        Optional<String> existingToken = Optional.of((String) redisTemplate.opsForValue().get(providerId));

        // Redis에 값이 없으면 INVALID 반환
        return existingToken
                .filter(token -> token.equals(refreshToken)) // refreshToken과 비교
                .map(token -> JwtStatus.ISSUED) // 일치하면 ISSUED 반환
                .orElseGet(() -> {
                    log.warn("JWT 토큰 에러: Redis에 저장된 토큰 없음 또는 불일치");
                    return JwtStatus.INVALID;
                });

    }

    public String getRefreshToken(String providerId) {
        return (String) redisTemplate.opsForValue().get(providerId);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(username);
    }
}
