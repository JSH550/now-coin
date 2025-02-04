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
@RequestMapping("/api/subscription")
public class SubscribeController {

    private final SubscriptionService subscribeService;

    public SubscribeController(SubscriptionService subscribeService) {
        this.subscribeService = subscribeService;
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
