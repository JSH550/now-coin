package com.coin.now_coin.common.jwt;

import com.coin.now_coin.member.MemberRole;
import com.coin.now_coin.member.dto.CreateMemberJwtDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        // ️쿠키에서 ACCESS_TOKEN 가져오기
        String accessToken = resolveTokenFromCookie(request, "ACCESS_TOKEN");
        //토큰 유무 확인, 없을경우 다음필터 진행
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("JWT 토큰이 없음 또는 비어 있음");
            filterChain.doFilter(request, response);
            return;
        }
        String providerId = jwtUtil.getClaimValue(accessToken, "providerId");
        log.info("이사람 Provider ID ={}",providerId);

        try {
            JwtStatus jwtStatus = jwtUtil.validateJwt(accessToken);//유효하지 않은 토큰일경우 토큰 삭제하기

            switch (jwtStatus) {
                case INVALID -> {
                    //유효하지 않은 토큰이면 삭제하고 로그인 재요청
                    log.warn("JWT 토큰이 유효하지 않음. 토큰 삭제 처리");
                    deleteAccessToken(response, "Access Token Expired. Please log in again.");
                    return;
                }
                case EXPIRED -> {
                    //엑세스 토큰이 만료되었을 경우, 리프레시 토큰 확인 후 재발급 시도
                    log.warn("엑세스 토큰 만료됨. 리프레시 토큰 확인 중...");


                    //Redis에서 리프레시 토큰 파싱
                    String refreshToken = refreshTokenService.getRefreshToken(providerId);

                    //리프레시 토큰 없으면 토큰 삭제
                    if (refreshToken == null || refreshToken.isBlank()) {
                        log.warn("리프레시 토큰 없음. 재로그인 필요.");
                        deleteAccessToken(response, "Refresh Token Missing. Please log in again.");
                        return;
                    }

                    //리프레시 토큰의 상태 확인
                    JwtStatus refreshTokenStatus = refreshTokenService.validateRefreshToken(refreshToken);

                    //리프레시 토큰이 유효하지 않을경우 토큰 삭제
                    if (refreshTokenStatus.equals(JwtStatus.INVALID)) {
                        deleteAccessToken(response, "Invalid Access Token. Please log in again");
                        return;
                    }
                    log.info("리프레시 토큰 유효함. 새로운 엑세스 토큰 발급...");

                    deleteCookie(response, "ACCESS_TOKEN");//엑세스 토큰 삭제
                    log.info("이사람이름임++++++++={}",jwtUtil.getClaimValue(refreshToken, "name"));
                    // 새 엑세스 토큰 생성
                    CreateMemberJwtDto newDto = CreateMemberJwtDto.builder()
                            .memberRole(MemberRole.READER)
                            .name(jwtUtil.getClaimValue(refreshToken, "name"))
                            .providerId(providerId)
                            .build();

                    String newAccessToken = jwtUtil.generateAccessToken(newDto);
                    Cookie jwtCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    jwtCookie.setMaxAge(3600);//쿠키유효시간 1시간
                    response.addCookie(jwtCookie);


                    // 사용자 인증 설정
                    saveMemberAuthentication(providerId, request);
                }
                case ISSUED -> {
                    //유저정보가 있고, SecurityContextHolder 에 유저정보가 없을경우
                    if (providerId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        log.info("JWT 인증 성공 - 사용자: {}", providerId);
                        // 사용자 인증 객체 생성
                        saveMemberAuthentication(providerId, request);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("JWT 처리 중 오류 발생: {}", ex.getMessage());
            deleteAccessToken(response, "Invalid Access Token. Please log in again.");
            return;
        }
        //다음필터진행
        filterChain.doFilter(request, response);
    }


    // 쿠키에서 JWT 토큰 추출
    private String resolveTokenFromCookie(HttpServletRequest request, String tokenName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                //엑세스 토큰을 쿠키리스트에서 가져옴
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue(); // JWT 토큰 반환
                }
            }
        }
        return null; // 쿠키에 JWT 토큰이 없는 경우
    }


    //엑세스 토큰과 리프레시 토큰 삭제후 에러 응답 반환
    private void deleteAccessToken(HttpServletResponse response, String message) throws IOException {
        deleteCookie(response, "ACCESS_TOKEN"); // 쿠키에서 삭제
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);//버퍼에 메시지 입력
        response.getWriter().flush();//유저에게 내용 전송
    }

    // 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
        log.info("쿠키 삭제 완료: {}", cookieName);
    }

    /**
     * SecurityContextHolder에 사용자 인증 정보 저장
     * @param providerId 유저의 provider ID
     * @param request 현재 HTTP Request
     */
    private void saveMemberAuthentication(String providerId, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        providerId, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);//SecurityContextHolder에 정보 저장
    }
}

