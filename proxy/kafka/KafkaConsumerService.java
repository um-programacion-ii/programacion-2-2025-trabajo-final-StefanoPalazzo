package com.stefanopalazzo.proxy.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RestTemplate restTemplate = new RestTemplate();

    @KafkaListener(topics = "eventos-sync", groupId = "proxy")
    public void escuchar(Object mensaje) {
        log.info("📥 Mensaje recibido desde Kafka: {}", mensaje);

        try {
            // 1) URL del backend (Docker pasa el backend como "backend")
            String url = "http://backend:8081/api/sync/evento";

            // 2) POST hacia backend
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> req = new HttpEntity<>(mensaje, headers);

            ResponseEntity<String> response =
                restTemplate.postForEntity(url, req, String.class);

            log.info("📤 Notificación enviada al backend. Respuesta: {}", response.getBody());

        } catch (Exception e) {
            log.error("❌ Error reenviando mensaje al backend: {}", e.getMessage());
        }
    }
}
