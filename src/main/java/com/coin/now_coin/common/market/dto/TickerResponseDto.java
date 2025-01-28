package com.coin.now_coin.common.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TickerResponseDto {

    @JsonProperty("market")
    private String market; // 예: KRW-BTC

    @JsonProperty("trade_price")
    private Double tradePrice; // 현재 가격

    @JsonProperty("high_price")
    private Double highPrice; // 24시간 최고가

    @JsonProperty("low_price")
    private Double lowPrice; // 24시간 최저가

    @JsonProperty("change")
    private String change; // 상승/하락 상태 (RISE/FALL)

    @JsonProperty("change_price")
    private Double changePrice; // 가격 변동 폭

    @JsonProperty("acc_trade_price_24h")
    private Double accTradePrice24h; // 24시간 누적 거래대금

    @JsonProperty("korean_name")
    private String koreanName; // 한글 이름 (API에서 직접 제공되지 않을 경우 추가 처리 필요)
}
