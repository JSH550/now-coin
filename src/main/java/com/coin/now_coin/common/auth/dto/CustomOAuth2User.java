package com.coin.now_coin.common.auth.dto;


import com.coin.now_coin.member.MemberRole;
import com.coin.now_coin.member.dto.MemberLoginDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final MemberLoginDto memberLoginDto;

    public CustomOAuth2User(MemberLoginDto memberLoginDto) {
        this.memberLoginDto = memberLoginDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return memberLoginDto.getName();
    }

    public String getEmail(){
        return memberLoginDto.getEmail();
    }
    public String getProviderId(){

        return memberLoginDto.getProviderId();


    }

    public MemberRole getRole(){
        return memberLoginDto.getMemberRole();
    }
}
