package com.coin.now_coin.subscription;

import com.coin.now_coin.subscription.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api/subscriptions")
public class SubscribeController {

    private final SubscriptionService subscribeService;

    public SubscribeController(SubscriptionService subscribeService) {
        this.subscribeService = subscribeService;
    }


    /**
     * 유저가 구독한 코인들을 전송하는 API
     * @param authentication 유저의 인증정보
     * @return JSON 형태로 데이터 반환
     */
    @GetMapping
    public ResponseEntity<?> getSubscriptionsData(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.ok("미로그인유저");
        }

        //유저의 ProviderId 파싱
        String providerId = authentication.getName();

        //유저가 구독한 코인들 List로 가져옴
        Map<String, Object> response = subscribeService.getMemberSubscriptionCoins(providerId);

        return ResponseEntity.ok(response);//JSON 형태로 반환(Map자료형)


    }
    /**
     * 유저의 코인 구독 정보를 변경하는 API
     * @param requestBody JSON 형태의 requestBody, market 정보 포함
     * @param authentication 유저의 인증정보
     * @return
     */
    @PostMapping
    public ResponseEntity<?> toggleSubscription(
            @RequestBody Map<String,String> requestBody,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("미로그인유저");
        }
        //Coin id 와 유저 Provdier Id 로 구독 정보 톱글
        subscribeService.toggleSubscriptions(requestBody.get("market"),authentication.getName());
        //정상적으로 완료되었음을 전송
        return ResponseEntity.ok("ok");
    }

}
