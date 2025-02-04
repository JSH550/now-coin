package com.coin.now_coin.member;

import com.coin.now_coin.common.auth.OAuthProvider;
import com.coin.now_coin.member.dto.MemberCreateDto;
import com.coin.now_coin.member.dto.MemberLoginDto;

public interface MemberService {

    Boolean isMember(OAuthProvider provider, String providerId);

    //없으면 저장하는 로직
    Boolean saveMember(MemberCreateDto memberCreateDto);


    //로그인에 필요한 정보를 전달하는 로직
    MemberLoginDto getLoginDetails(OAuthProvider provider, String providerId);




    Member getMemberByProviderId(String providerId);



}
