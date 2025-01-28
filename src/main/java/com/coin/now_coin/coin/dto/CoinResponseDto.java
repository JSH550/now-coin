package com.coin.now_coin.coin.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoinResponseDto {

    private String name;        // 코인 이름(한글명)
    private String market;      // 코인 심볼 (예: KRW-BTC)
    private Double currentPrice; // 현재 가격
    private Double changeRate;  // 변동률 (당일) (예: 5.23%)
    private Double changeAmount; // 변동 금액 (당일) (예: 1000.0 KRW)
    private Double tradeVolume24h; // 거래 금액 (24시간) (예: 120000000 KRW) 백만단위
    private String change;// 보합 상승 하락



    //변동률(당일)
    //거래금액(24시간)
}
