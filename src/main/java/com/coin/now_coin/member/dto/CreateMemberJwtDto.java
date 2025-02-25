package com.coin.now_coin.member.dto;

import com.coin.now_coin.member.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateMemberJwtDto {

    private MemberRole memberRole;//ex: MANAGER
    private String name;//ex: John
    private String providerId;


    @Builder
    public CreateMemberJwtDto(MemberRole memberRole, String name, String providerId) {
        this.memberRole = memberRole;
        this.name = name;
        this.providerId = providerId;
    }
}
