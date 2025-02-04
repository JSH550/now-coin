package com.coin.now_coin.subscription;


import com.coin.now_coin.coin.Coin;
import com.coin.now_coin.member.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Subscription {


    @EmbeddedId
    private SubscriptionId id;

    @ManyToOne
    @MapsId("memberId") // SubscribeId의 memberId와 매핑
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("coinId") // SubscribeId의 coinId와 매핑
    @JoinColumn(name = "coin_id")
    private Coin coin;


    public Subscription(SubscriptionId id, Member member, Coin coin) {
        this.id = id;
        this.member = member;
        this.coin = coin;
    }
}
