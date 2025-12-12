package com.stefanopalazzo.proxy.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final RestTemplate restTemplate;

    @Value("${proxy.backend-url}")
    private String backendUrl;

    public KafkaConsumerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Proxy recibió mensaje de Kafka: {}", message);
        try {
            String url = backendUrl + "/api/internal/eventos/actualizacion";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(message, headers);
            restTemplate.postForObject(url, request, String.class);
            log.info("Notificación enviada al backend: {}", url);
        } catch (Exception e) {
            log.error("Error al notificar al backend", e);
        }
    }
}
