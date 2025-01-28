package com.coin.now_coin.common.market.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CandleDto {
    private String market; // 시장 코드 (예: KRW-BTC)
    private String candle_date_time_utc; // UTC 기준 시간
    private String candle_date_time_kst; // KST 기준 시간
    private double opening_price; // 시가
    private double high_price;    // 고가
    private double low_price;     // 저가
    private double trade_price;   // 종가
    private double candle_acc_trade_price; // 누적 거래대금
    private double candle_acc_trade_volume; // 누적 거래량
    private int unit; // 캔들 단위 (분봉의 경우)
}
