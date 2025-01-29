package com.coin.now_coin.common.auth;


import com.coin.now_coin.common.auth.dto.CustomOAuth2User;
import com.coin.now_coin.member.MemberRole;
import com.coin.now_coin.member.dto.CreateMemberJwtDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler  {


    @Value("${frontend.domain}")
    private String domain;


    private final JwtUtil jwtUtil;

    private final  RefreshTokenService refreshTokenService;

    public CustomSuccessHandler(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String name = customUserDetails.getName();//get user name
        MemberRole role = customUserDetails.getRole();//역할 파싱
        String providerId = customUserDetails.getProviderId();//provider ID 파싱


        //엑세스 토큰을 만들기 위한 DTO 생성
        CreateMemberJwtDto build = CreateMemberJwtDto.builder()
                .name(name)
                .memberRole(role)
                .providerId(providerId)
                .build();

        String accessToken = jwtUtil.generateAccessToken(build);//엑세스 토큰 생성

        // 엑세스 토큰 생성
        Cookie jwtCookie = new Cookie("ACCESS_TOKEN", accessToken);
        jwtCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        jwtCookie.setPath("/"); // 전체 도메인에서 쿠키 유효
        jwtCookie.setMaxAge((int) jwtUtil.getExpirationTime()); // 쿠키 만료 시간 (초 단위)



        //리프레시 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(providerId);
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN",refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        jwtCookie.setMaxAge((int) jwtUtil.getExpirationTime()); // 쿠키 만료 시간 (초 단위)
        refreshTokenService.saveRefreshTokenToRedis(providerId,refreshToken);//Redis에 토큰 저장

        response.addCookie(jwtCookie);//응답에 엑세스 토큰 추가
        response.addCookie(refreshCookie);//응답에 리프레시 토큰 추가
        response.sendRedirect(domain);//응답 반환



    }



}
