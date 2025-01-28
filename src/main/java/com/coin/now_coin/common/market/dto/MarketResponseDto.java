package com.coin.now_coin.common.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketResponseDto {

    @JsonProperty("market")
    private String market; // 예: KRW-BTC

    @JsonProperty("korean_name")
    private String koreanName; // 예: 비트코인

    @JsonProperty("english_name")
    private String englishName; // 예: Bitcoin
}
