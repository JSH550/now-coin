package com.coin.now_coin.member;

import com.coin.now_coin.common.auth.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query("select m  " +
            "from Member m  " +
            "where m.oauthProvider = :provider " +
            "and m.providerId = :providerId ")
    Optional<Member> findByOauthProviderAndProviderId(@Param("provider") OAuthProvider provider,
                                                      @Param("providerId") String providerId);

}
