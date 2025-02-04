package com.coin.now_coin.subscription.service;

import java.util.Map;

public interface SubscriptionService {


    /**
     * 사용자의 구독 상태를 토글하는 메서드.
     * <p>
     * 해당 사용자가 특정 코인을 이미 구독하고 있다면 구독을 취소하고,
     * 구독하지 않았다면 새로운 구독을 추가합니다.
     *</p>
     * @param market     구독할 코인의 Market (예:KRW-BTC)
     * @param providerId 사용자의 Provider ID (소셜 로그인 등과 연동된 ID)
     */
    void toggleSubscriptions(String market, String providerId);


    /**
     * 회원의 구독한 코인 목록을 조회하여 JSON 형태로 반환합니다.
     *
     * @param providerId OAuth2 제공자 ID
     * @return 회원이 구독한 코인의 목록을 포함한 응답 객체 (status, markets, count 포함)
     */
    Map<String, Object> getMemberSubscriptionCoins(String providerId);

}
