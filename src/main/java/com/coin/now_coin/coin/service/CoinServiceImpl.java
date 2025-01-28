package com.coin.now_coin.coin.service;

import com.coin.now_coin.coin.Coin;
import com.coin.now_coin.coin.CoinRepository;
import com.coin.now_coin.coin.dto.CoinResponseDto;
import com.coin.now_coin.common.kafka.KafkaMessageProducer;
import com.coin.now_coin.common.market.MarketService;
import com.coin.now_coin.common.market.dto.MarketDto;
import com.coin.now_coin.subscription.SubscribedCoinDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;


    private final MarketService marketService;

    public CoinServiceImpl(CoinRepository coinRepository, MarketService marketService, KafkaMessageProducer kafkaMessageProducer) {
        this.coinRepository = coinRepository;
        this.marketService = marketService;
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    private final KafkaMessageProducer kafkaMessageProducer;



    //주기적으로 코인정보를 전송하는 로직
    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void fetchAndBroadcastCoinPrice() {


        //외부에서 코인정보 가져오는 API 필요...

        List<CoinResponseDto> fullCoinInfo = marketService.getFullCoinInfo();

        //클라이언트 전달을위한 Map 자료형
        Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("type","all_coins");//모든 코인정보를 담은 메시지라는것을 명시
        messageMap.put("data",fullCoinInfo);//코인정보 넣기
        // JSON 변환을 위한 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // fullCoinInfo를 JSON 형식으로 변환
            String jsonMessage = objectMapper.writeValueAsString(messageMap);

            // WebSocket 클라이언트에게 JSON 메시지 전송
//            webSocketSessionService.broadcastMessage(jsonMessage);

            kafkaMessageProducer.sendBroadcastMessage(jsonMessage);
        } catch (JsonProcessingException e) {
            // JSON 변환 실패 시 에러 처리
            log.error("에러임={}", e.getMessage());
        }

    }

    @Scheduled(fixedRate = 150000) // 5초마다 실행

    public void fetchAndBroadcastSurgeCoinInfo(){

        try {

            List<CoinResponseDto> surgingCoins = marketService.getSurgingCoins();

            ObjectMapper objectMapper = new ObjectMapper();

            Map<String,Object> messageMap = new HashMap<>();
            messageMap.put("type","surging_coins");//모든 코인정보를 담은 메시지라는것을 명시
            messageMap.put("data",surgingCoins);//코인정보 넣기

            // fullCoinInfo를 JSON 형식으로 변환
            String jsonMessage = objectMapper.writeValueAsString(messageMap);

            log.info("바꾼정보={}",jsonMessage);



            kafkaMessageProducer.sendBroadcastMessage(jsonMessage);
        } catch (JsonProcessingException e) {
            // JSON 변환 실패 시 에러 처리
            log.error("에러임={}", e.getMessage());
        }



        //코인 정보들 받아오고

        //그중에 5% 이상 급등한거있으면 카프카로 메시지보내자!


    }


    @Override
    public List<SubscribedCoinDto> getCoinListByMemberId(Long memberId) {
        //유저가 구독한 코인들 가져옴
        List<Coin> coinList = coinRepository.getCoinByMemberId(memberId);

        //DTo 형태로 변환하여 반환
        return convertToCoinDto(coinList);
    }


    @Override
    public List<String> getCoinSymbolsByMember(String providerId) {
        return coinRepository.getCoinByProviderId(providerId);//유저가 구독한 코인의 심볼을 반환
    }

    @Override
    @Transactional
    public boolean createCoins() {

        try {
            List<MarketDto> allMarkets = marketService.getAllMarkets();
            //DTO 를 엔티티 객체로 변환하여 List에 저장
            List<Coin> coinList = allMarkets.stream()
                    .map(dto -> Coin.builder()
                            .market(dto.getMarket())
                            .englishName(dto.getEnglish_name())
                            .koreanName(dto.getKorean_name())
                            .build()).toList();
            coinRepository.saveAll(coinList);//엔티티 저장

            return true;//성공했음을 알림
        } catch (Exception ex) {
            log.error("createCoins 에러발생 ={}", ex.getMessage());
            return false;
        }

    }


    private List<SubscribedCoinDto> convertToCoinDto(List<Coin> coinList) {

        return coinList.stream().map(
                        c -> SubscribedCoinDto.builder()
                                .market(c.getMarket())
                                .id(c.getId())
                                .englishName(c.getEnglishName())
                                .build())

                .collect(Collectors.toList());


    }
}
