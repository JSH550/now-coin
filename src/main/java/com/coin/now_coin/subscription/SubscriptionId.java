package com.coin.now_coin.subscription;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscriptionId implements Serializable {


    private Long memberId; // Member 테이블의 기본 키

    private Long coinId;   // Coin 테이블의 기본 키

    public SubscriptionId(Long userId, Long coinId) {
        this.memberId = userId;
        this.coinId = coinId;
    }

    public SubscriptionId() {

    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionId that = (SubscriptionId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(coinId, that.coinId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, coinId);
    }



}
