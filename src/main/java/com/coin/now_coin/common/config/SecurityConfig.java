package com.coin.now_coin.common.config;


import com.coin.now_coin.common.auth.CustomOAuth2UserService;
import com.coin.now_coin.common.auth.CustomSuccessHandler;
import com.coin.now_coin.common.jwt.JwtAuthenticationFilter;
import com.coin.now_coin.common.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;
    private final CustomSuccessHandler customSuccessHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, JwtUtil jwtUtil, CustomSuccessHandler customSuccessHandler, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtUtil = jwtUtil;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    //서블릿 필터 무시 URL 정적 리소스 등록 필수
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //HTTP Basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**","/html/**","/ws/**","/api/**","/oauth2/**","/test").permitAll() // 정적 리소스 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );

        //O
        http.oauth2Login((login) -> login
                .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)))
                .successHandler(customSuccessHandler)
        );

        //JWT 인증필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //세션관리 STATELESS
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
