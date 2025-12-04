package com.stefanopalazzo.proxy.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "eventos-sync", groupId = "proxy")
    public void escuchar(Object mensaje) {
        log.info("Mensaje desde Kafka: {}", mensaje);
    }
}
