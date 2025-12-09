package com.stefanopalazzo.proxy.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafka;

    public void enviarSyncEvento(Object data) {
        kafka.send("eventos-sync", data);
    }
}
