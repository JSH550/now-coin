package com.coin.now_coin.coin.service;

import com.coin.now_coin.coin.Coin;
import com.coin.now_coin.subscription.SubscribedCoinDto;

import java.util.List;

public interface CoinService {



    List<SubscribedCoinDto> getCoinListByMemberId(Long memberId);




    //유저가 구독한 코인의 심볼을 반환 예 KRW-BTC
    List<String> getCoinSymbolsByMember(String providerId);


    boolean createCoins();

    Coin getCoinByMarket(String market);


}
