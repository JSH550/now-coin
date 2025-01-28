package com.coin.now_coin.common.market;

import com.coin.now_coin.coin.dto.CoinResponseDto;
import com.coin.now_coin.common.market.dto.CandleDto;
import com.coin.now_coin.common.market.dto.MarketDto;
import com.coin.now_coin.common.market.dto.TickerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MarketServiceImpl implements MarketService {

    private static final String BASE_URL = "https://api.upbit.com/v1";
    private final RestTemplate restTemplate;

    public MarketServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 거래소의 모든 코인 정보를 가져오는 메서드(예시: KRW-BTC)
     *
     * @return 모든 MarketDto 리스트
     */
    @Override
    public List<MarketDto> getAllMarkets() {
        String url = BASE_URL + "/market/all";//요청 URL
        MarketDto[] response = restTemplate.getForObject(url, MarketDto[].class);//DTO로 변환
        return Arrays.asList(response); //정보 반환
    }

    /**
     * 가상화폐 마켓의 실시간 정보를 가져오는 메서드
     *
     * @param markets 가상화폐 시장 코드 (가상화폐 예시: KRW-BTC)
     * @return TickerDto 실시간 정보
     */
    @Override
    public List<TickerDto> getTickerData(List<String> markets) {

        //리스트로 받은 마켓들의 이름을 , 으로 합침 (KRW-BTC,KRW-KEM)
        String marketParam = String.join(",", markets);

        //요청 URL, 정보를 요청할 마켓들의 이름을 포함하여 요청
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/ticker")
                .queryParam("markets", marketParam)
                .toUriString();

        //받아온 데이터를 DTO로 파싱
        TickerDto[] response = restTemplate.getForObject(url, TickerDto[].class);

        //반환
        return Arrays.stream(response)
                .map(this::formatTickerDto) // 소수점 포맷 적용
                .collect(Collectors.toList());
    }

    /**
     * TickerDto의 소수점 값을 포맷팅하는 유틸리티 메서드
     * @param dto TickerDto 객체
     * @return 포맷팅된 TickerDto 객체
     */
    private TickerDto formatTickerDto(TickerDto dto) {
        dto.setTrade_price(round(dto.getTrade_price(), 2)); // 현재가 소수점 2자리
        dto.setSigned_change_rate(round(dto.getSigned_change_rate() * 100, 2)); // 변동률 % 소수점 2자리
        dto.setSigned_change_price(round(dto.getSigned_change_price(), 2)); // 변동 금액 소수점 2자리
        dto.setAcc_trade_price_24h(round(dto.getAcc_trade_price_24h() / 1_000_000, 0)); // 거래 대금은 정수 백만원 단위
        return dto;
    }



    /**
     * 소수점 반올림 유틸리티 메서드
     * @param value 입력 값
     * @param places 반올림 소수점 자리수
     * @return 반올림된 값
     */
    private Double round(Double value, int places) {
        if (value == null) return null; // null 값 처리
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    /**
     * 전체 코인 정보(시장 정보 + 실시간 정보)를 통합하여 반환
     * @return CoinResponseDto 리스트 (전체 코인 정보)
     */
    @Override
    // 전체 코인 정보 통합
    public List<CoinResponseDto> getFullCoinInfo() {
        // 1. 전체 시장 정보 가져오기
        List<MarketDto> markets = getAllMarkets();

        // 2. 시장 코드만 추출
        List<String> marketCodes = markets.stream()
                .map(MarketDto::getMarket)
                .filter(market -> market.startsWith("KRW")) // 원화 기준 코인만 필터링
                .collect(Collectors.toList());

        // 3. 실시간 데이터 가져오기
        List<TickerDto> tickers = getTickerData(marketCodes);
        // 4. 시장 정보와 실시간 데이터를 결합
        return convertCoinResponseDtos(markets, tickers);
    }

    /**
     * 급등 코인 정보를 반환
     * 3% 이상 급등한 코인 필터링
     * @return CoinResponseDto 리스트 (급등 코인 정보)
     */
    @Override
    public List<CoinResponseDto> getSurgingCoins() {
        // 1. 전체 시장 정보 가져오기
        List<MarketDto> markets = getAllMarkets();

        // 2. 시장 코드만 추출
        List<String> marketCodes = markets.stream()
                .map(MarketDto::getMarket)
                .filter(market -> market.startsWith("KRW")) // 원화 기준 코인만 필터링
                .collect(Collectors.toList());

        ;

        // 3. 실시간 데이터 가져오기( 3% 이상 급등한 코인 필터링)
        List<TickerDto> tickers = getTickerData(marketCodes).stream()
                .filter(tickerDto -> tickerDto.getSigned_change_rate() > 3) // 3% 이상 급등한 코인 필터링
                .toList();
        // 4. 시장 정보와 실시간 데이터를 결합
        return convertCoinResponseDtos(markets, tickers);
    }

    /**
     * 시장 정보와 실시간 데이터를 결합하여 CoinResponseDto 리스트로 변환
     * @param markets MarketDto 리스트
     * @param tickers TickerDto 리스트
     * @return CoinResponseDto 리스트
     */
    private List<CoinResponseDto> convertCoinResponseDtos(List<MarketDto> markets, List<TickerDto> tickers) {

        return tickers.stream()
                .map(ticker -> {
                    // ticker와 market의 정보를 결합
                    MarketDto market = markets.stream()
                            .filter(m -> m.getMarket().equals(ticker.getMarket())) // Ticker의 시장과 일치하는 Market 찾기
                            .findFirst()
                            .orElse(null);

                    return CoinResponseDto.builder()
                            .name(market != null ? market.getKorean_name() : "Unknown") // Market이 존재하면 이름 사용
                            .market(ticker.getMarket()) // Ticker의 시장 코드
                            .currentPrice(ticker.getTrade_price()) // Ticker의 현재 가격
                            .changeRate(ticker.getSigned_change_rate()) // Ticker의 변동률
                            .changeAmount(ticker.getSigned_change_price()) // Ticker의 변동 금액
                            .tradeVolume24h(ticker.getAcc_trade_price_24h()) // Ticker의 24시간 거래 금액
                            .change(ticker.getChange() != null ? ticker.getChange() : "NoData") // Ticker의 상태
                            .build();
                }).collect(Collectors.toList());
    }


    /**
     * 특정 코인의 캔들 데이터를 가져오는 메서드
     * @param market 코인 마켓 (예: "KRW-BTC")
     * @param timeframe 캔들 데이터의 시간 단위 (예: "minutes/1", "days" 등)
     * @param count 가져올 데이터 개수
     * @return CandleDto 리스트 (캔들 데이터)
     */
    @Override
    public List<?> getCoinCandle(String market, String timeframe, int count) {
        // timeframe: minutes/1, minutes/5, days, weeks, months 등
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/candles/" + timeframe)
                .queryParam("market", market) // 코인 심볼
                .queryParam("count", count)  // 가져올 데이터 개수
                .toUriString();
        try {
            // API 호출
            CandleDto[] response = restTemplate.getForObject(url, CandleDto[].class);
            return Arrays.asList(response); // 배열을 리스트로 변환하여 반환
        } catch (Exception e) {
            throw new RuntimeException("캔들 데이터 요청 실패: " + e.getMessage(), e);
        }
    }


}
