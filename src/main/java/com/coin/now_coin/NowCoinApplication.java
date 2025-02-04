package com.coin.now_coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class NowCoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(NowCoinApplication.class, args);
	}

}
