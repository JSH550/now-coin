package com.coin.now_coin.subscription;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Builder
@Getter
@Setter
@ToString
public class SubscribedCoinDto {

    private Long id;

    private String englishName;    // 코인 이름 (예: Bitcoin)

    private String koreanName;

    private String market;

}
