package com.coin.now_coin.common.market.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TickerDto {

    private String market;              // 코인 심볼 (KRW-BTC)
    private Double trade_price;          // 현재가
    private Double signed_change_rate;    // 전일대비 변동률
    private Double signed_change_price;   // 전일대비 변동금액
    private Double acc_trade_price_24h;    // 24시간 거래대금
    private String change;// 보합 상승 하락

}
