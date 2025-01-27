package com.coin.now_coin.member;

import com.coin.now_coin.common.auth.OAuthProvider;
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
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    @Column(nullable = false, unique = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;//OAuth2 제공자, google, naver... etc
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;//ADMIN, READER, MANAGER.. etc





    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder
    public Member(String email, String name, String providerId, OAuthProvider oauthProvider, MemberRole memberRole) {
        this.email = email;
        this.name = name;
        this.providerId = providerId;
        this.oauthProvider = oauthProvider;
        this.memberRole = memberRole;
    }
}
