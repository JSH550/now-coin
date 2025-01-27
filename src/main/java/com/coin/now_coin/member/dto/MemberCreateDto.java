package com.coin.now_coin.member.dto;

import com.coin.now_coin.common.auth.OAuthProvider;
import com.coin.now_coin.member.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberCreateDto {

    private String email;//ex:example@google.com

    private OAuthProvider provider;//ex: google

    private MemberRole memberRole;//ex: MANAGER
    private String name;//ex: John

    private String providerId;//ex : dkwoe323

    @Builder
    public MemberCreateDto(String email, OAuthProvider provider, MemberRole memberRole, String name, String providerId) {
        this.email = email;
        this.provider = provider;
        this.memberRole = memberRole;
        this.name = name;
        this.providerId = providerId;
    }
}
