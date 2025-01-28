package com.coin.now_coin.common.market;

import com.coin.now_coin.coin.dto.CoinResponseDto;
import com.coin.now_coin.common.market.dto.MarketDto;
import com.coin.now_coin.common.market.dto.TickerDto;

import java.util.List;

public interface MarketService {

    /**
     * 거래소의 모든 코인 정보를 가져오는 메서드(예시: KRW-BTC)
     *
     * @return 모든 MarketDto 리스트
     */
     List<MarketDto> getAllMarkets();

    /**
     * 가상화폐 마켓의 실시간 정보를 가져오는 메서드
     *
     * @param markets 가상화폐 시장 코드 (가상화폐 예시: KRW-BTC)
     * @return TickerDto 실시간 정보
     */
     List<TickerDto> getTickerData(List<String> markets);

    /**
     * 전체 코인 정보(시장 정보 + 실시간 정보)를 통합하여 반환
     * @return CoinResponseDto 리스트 (전체 코인 정보)
     */
     List<CoinResponseDto> getFullCoinInfo();


    /**
     * 급등 코인 정보를 반환
     * 3% 이상 급등한 코인 필터링
     * @return CoinResponseDto 리스트 (급등 코인 정보)
     */
    List<CoinResponseDto> getSurgingCoins();

    /**
     * 특정 코인의 캔들 데이터를 가져오는 메서드
     * @param market 코인 심볼 (예: "KRW-BTC")
     * @param timeframe 캔들 데이터의 시간 단위 (예: "minutes/1", "days" 등)
     * @param count 가져올 데이터 개수
     * @return CandleDto 리스트 (캔들 데이터)
     */
    List<?> getCoinCandle(String market, String timeframe, int count);




}
