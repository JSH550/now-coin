package com.coin.now_coin.common.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    //로그인 로직

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login/google")
    public String redirectGoogleLogin(){
        return "redirect:/oauth2/authorization/google";
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken){

        if (!jwtUtil.validateJwt(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

//        //리프레시 토큰에서 유저정보 추출
//        String providerId = jwtUtil.getClaimValue(refreshToken, "providerId");
//        //Redis에서 정보 가져옴
//        String storedRefreshToken = refreshTokenService.getRefreshToken(username);
//
//        if (!refreshToken.equals(storedRefreshToken)) {
//            return ResponseEntity.status(401).body("Refresh token mismatch");
//        }
//
//        String newAccessToken = jwtUtil.generateAccessToken(username);
//        return ResponseEntity.ok(newAccessToken);


        return null;

    }
    //로그아웃 로직

    //
}
