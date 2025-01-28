package com.coin.now_coin.common.market.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MarketDto {

    private String market;       // 코인 마켓 (KRW-BTC)
    private String korean_name;   // 한글 이름 (비트코인)
    private String english_name;  // 영어 이름 (Bitcoin)
}
