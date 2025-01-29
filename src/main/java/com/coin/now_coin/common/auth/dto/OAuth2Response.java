package com.coin.now_coin.common.auth.dto;


import com.coin.now_coin.common.auth.OAuthProvider;

public interface OAuth2Response {


    OAuthProvider getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
