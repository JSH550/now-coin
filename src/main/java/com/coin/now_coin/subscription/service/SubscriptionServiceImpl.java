package com.coin.now_coin.subscription.service;

import com.coin.now_coin.coin.Coin;
import com.coin.now_coin.coin.service.CoinService;
import com.coin.now_coin.member.Member;
import com.coin.now_coin.member.MemberService;
import com.coin.now_coin.subscription.Subscription;
import com.coin.now_coin.subscription.SubscriptionId;
import com.coin.now_coin.subscription.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final CoinService coinService;

    private final MemberService memberService;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, CoinService coinService, MemberService memberService) {
        this.subscriptionRepository = subscriptionRepository;
        this.coinService = coinService;
        this.memberService = memberService;
    }


    @Override
    @Transactional
    public void toggleSubscriptions(String market, String providerId) {

        Member member = memberService.getMemberByProviderId(providerId);//멤버 정보 조회

        Coin coin = coinService.getCoinByMarket(market);//코인 정보 조회

        SubscriptionId subscriptionId = new SubscriptionId(member.getId(), coin.getId());//복합키 생성

        Optional<Subscription> subscriptionById = getSubscriptionById(subscriptionId);//DB에서 구독정보 조회

        if (subscriptionById.isPresent()) {
            // 구독이 이미 존재하면 삭제 (토글 방식)
            deleteSubscriptions(subscriptionById.get());
            log.info("구독 정보 삭제: memberId={}, market={}", member.getId(), market);
        } else {
            // 구독이 없으면 새로운 구독 생성
            Subscription subscription = new Subscription(subscriptionId, member, coin);
            subscriptionRepository.save(subscription);
            log.info("구독 정보 생성: memberId={}, market={}", member.getId(), market);
        }
    }

    @Override
    public Map<String, Object> getMemberSubscriptionCoins(String providerId) {
        List<String> coinsNameList = coinService.getCoinNamesByProviderId(providerId);

        //JSON 형태로 보내기위한 Map 객체
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("markets", coinsNameList);
        response.put("count", coinsNameList.size());

        return response;//반환

    }

    /**
     * SubscriptionId 로 구독 레코드를 찾는 메서드,
     * OPtional로 감싸 반환, Null체크는 사용하는 메서드에서 진행
     *
     * @param subscriptionId Member ID 와 Coin ID 로 구성된 복합키
     * @return Optional<Subscription>
     */
    private Optional<Subscription> getSubscriptionById(SubscriptionId subscriptionId) {
        return subscriptionRepository.findById(subscriptionId);
    }

    @Transactional
    protected void deleteSubscriptions(Subscription subscription) {
        subscriptionRepository.delete(subscription);
    }


}
