package com.coin.now_coin.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin,Long> {




    @Query("select c " +
            "from Coin c  " +
            "join c.subscriptions s " +
            "WHERE s.member.id = :memberId")
    List<Coin> getCoinByMemberId(@Param("memberId")Long memberId);


    @Query("select c.market " +
            "from Coin c  " +
            "join c.subscriptions s " +
            "WHERE s.member.providerId = :providerId")
    List<String> getCoinByProviderId(@Param("providerId")String providerId);


    @Query("select c " +
            "from Coin c " +
            "where c.market = :market")
    Optional<Coin> getCoinByMarket(@Param("market") String market);





}
