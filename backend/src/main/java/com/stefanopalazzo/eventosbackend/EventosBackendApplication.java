package com.stefanopalazzo.eventosbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventosBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventosBackendApplication.class, args);
    }

}

