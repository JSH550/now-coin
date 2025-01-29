package com.coin.now_coin.common.auth;


import com.coin.now_coin.common.auth.dto.CustomOAuth2User;
import com.coin.now_coin.common.auth.dto.GoogleResponse;
import com.coin.now_coin.common.auth.dto.NaverResponse;
import com.coin.now_coin.common.auth.dto.OAuth2Response;
import com.coin.now_coin.member.MemberRole;
import com.coin.now_coin.member.MemberService;
import com.coin.now_coin.member.dto.MemberCreateDto;
import com.coin.now_coin.member.dto.MemberLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Autowired
    public CustomOAuth2UserService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        //extract OAuth2 Provider
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        switch (provider) {
            case ("google") -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            case ("naver") -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }

        if (oAuth2Response == null) {
            throw new OAuth2AuthenticationException("404");
        }

        //DB에서 있는지확인
        Boolean isMember = memberService.isMember(oAuth2Response.getProvider(), oAuth2Response.getProviderId());

        log.info("유저정보={}",oAuth2Response.getProviderId());
        if (!isMember) {
            //멤버 새로만드는 로직 호출
            MemberCreateDto build = MemberCreateDto.builder()
                    .name(oAuth2Response.getName())
                    .provider(oAuth2Response.getProvider())
                    .memberRole(MemberRole.READER)
                    .email(oAuth2Response.getEmail())
                    .providerId(oAuth2Response.getProviderId())
                    .build();
            memberService.saveMember(build);
        }

        //멤버 정보 불러오는 로직 호출
        MemberLoginDto memberLoginDto = memberService.getLoginDetails(
                oAuth2Response.getProvider(),
                oAuth2Response.getProviderId()
        );




        return new CustomOAuth2User(memberLoginDto);


    }


}
