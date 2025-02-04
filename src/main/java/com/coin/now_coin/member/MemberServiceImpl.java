package com.coin.now_coin.member;

import com.coin.now_coin.common.auth.OAuthProvider;
import com.coin.now_coin.member.dto.MemberCreateDto;
import com.coin.now_coin.member.dto.MemberLoginDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;


    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Boolean isMember(OAuthProvider provider, String providerId) {
        return memberRepository.findByOauthProviderAndProviderId(provider, providerId).isPresent();
    }

    @Override
    @Transactional
    public Boolean saveMember(MemberCreateDto memberCreateDto) {

        try {
            Member member = Member.builder()
                    .memberRole(memberCreateDto.getMemberRole())
                    .name(memberCreateDto.getName())
                    .oauthProvider(memberCreateDto.getProvider())
                    .email(memberCreateDto.getEmail())
                    .providerId(memberCreateDto.getProviderId())
                    .build();
            memberRepository.save(member);

            return true;
        } catch (Exception ex) {
            log.info("save Member error" + ex.getMessage());
            return false;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public MemberLoginDto getLoginDetails(OAuthProvider provider, String providerId) {

        // Find member records from DB
        Member member = memberRepository.findByOauthProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user information."));

        // Converts to DTO and return
        return MemberLoginDto.builder()
                .memberRole(member.getMemberRole())
                .email(member.getEmail())
                .provider(member.getOauthProvider())
                .name(member.getName())
                .providerId(member.getProviderId())
                .build();

    }

    @Override
    public Member getMemberByProviderId(String providerId) {

        return memberRepository.findMemberByProviderId(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Member 엔티티 정보가 없습니다."));
    }


}
