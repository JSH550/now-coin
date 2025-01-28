package com.coin.now_coin.coin;

import com.coin.now_coin.subscription.Subscription;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Coin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String koreanName;    // 코인 이름 (예: Bitcoin)

    private String englishName;
    private String market;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder
    public Coin(String koreanName, String englishName, String market) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.market = market;
    }
}
