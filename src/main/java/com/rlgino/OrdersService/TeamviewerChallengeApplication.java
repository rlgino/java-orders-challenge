package com.rlgino.OrdersService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TeamviewerChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamviewerChallengeApplication.class, args);
	}

}
