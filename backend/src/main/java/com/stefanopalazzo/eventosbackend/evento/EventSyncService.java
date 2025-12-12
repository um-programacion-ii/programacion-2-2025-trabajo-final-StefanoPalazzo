package com.stefanopalazzo.eventosbackend.evento;

import com.stefanopalazzo.eventosbackend.catedra.client.CatedraClient;
import com.stefanopalazzo.eventosbackend.catedra.dto.EventoCompletoDto;
import com.stefanopalazzo.eventosbackend.catedra.dto.EventoResumidoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventSyncService {

    private static final Logger log = LoggerFactory.getLogger(EventSyncService.class);

    private final CatedraClient catedraClient;
    private final EventoRepository eventoRepository;
    private final RestTemplate restTemplate;

    @Value("${app.proxy-url}")
    private String proxyUrl;

    public void sincronizarEventoIndividual(Object data) {
        System.out.println("📥 Backend recibió evento individual: " + data);
        // TODO: parsear el objeto, mapear a Evento y guardarlo.
    }

    public void handleUpdate(String message) {
        log.info("Procesando actualización de Kafka: {}", message);

        try {
            // Parse the Kafka message to extract event ID
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(message);

            // Check if message contains eventoId
            if (root.has("eventoId")) {
                Long eventoId = root.get("eventoId").asLong();
                log.info("Sincronizando evento individual: {}", eventoId);
                sincronizarEventoIndividual(eventoId);
            } else {
                // If no specific event ID, sync all events
                log.info("Mensaje sin eventoId específico, sincronizando todos los eventos");
                sincronizar();
            }
        } catch (Exception e) {
            log.error("Error al procesar actualización de Kafka: {}", e.getMessage(), e);
            // Fallback: sync all events if parsing fails
            try {
                sincronizar();
            } catch (Exception syncError) {
                log.error("Error en sincronización de respaldo: {}", syncError.getMessage());
            }
        }
    }

    public Map<String, String> getSeatMapFromProxy(int eventoId) {
        try {
            String url = proxyUrl + "/proxy/asientos/" + eventoId;
            String json = restTemplate.getForObject(url, String.class);
            return parseSeatMap(json);
        } catch (Exception e) {
            log.error("Error fetching seat map from proxy", e);
            return Collections.emptyMap();
        }
    }

    public boolean bloquearAsientos(int eventoId, List<Map<String, Integer>> asientos) {
        try {
            String url = proxyUrl + "/proxy/bloquear-asientos";
            Map<String, Object> body = new HashMap<>();
            body.put("eventoId", eventoId);
            body.put("asientos", asientos);

            String response = restTemplate.postForObject(url, body, String.class);
            // Check if response contains "resultado": true
            return response != null && response.contains("\"resultado\":true");
        } catch (Exception e) {
            log.error("Error calling proxy to block seats", e);
            return false;
        }
    }

    public boolean realizarVenta(int eventoId, double precioVenta, List<Map<String, Object>> asientos) {
        try {
            String url = proxyUrl + "/proxy/realizar-venta";
            Map<String, Object> body = new HashMap<>();
            body.put("eventoId", eventoId);
            body.put("fecha", LocalDateTime.now().toString());
            body.put("precioVenta", precioVenta);
            body.put("asientos", asientos);

            String response = restTemplate.postForObject(url, body, String.class);
            return response != null && response.contains("\"resultado\":true");
        } catch (Exception e) {
            log.error("Error calling proxy to sell seats", e);
            return false;
        }
    }

    private Map<String, String> parseSeatMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode asientos = root.get("asientos");
            if (asientos != null && asientos.isArray()) {
                for (JsonNode node : asientos) {
                    int f = node.get("fila").asInt();
                    int c = node.get("columna").asInt();
                    String estado = node.get("estado").asText();
                    map.put(f + "_" + c, estado);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing seat map JSON", e);
        }
        return map;
    }

    public int sincronizar() {

        List<EventoResumidoDto> lista;

        try {
            lista = catedraClient.obtenerEventosResumidos();
        } catch (Exception e) {
            System.err.println("No se pudo sincronizar eventos: " + e.getMessage());
            return 0;
        }

        int count = 0;

        for (var resumido : lista) {

            EventoCompletoDto full = catedraClient.obtenerEventoCompleto(resumido.getId());

            Evento evento = Evento.builder()
                    .id(full.getId())
                    .titulo(full.getTitulo())
                    .resumen(full.getResumen())
                    .descripcion(full.getDescripcion())
                    .fecha(full.getFecha())
                    .precioEntrada(full.getPrecioEntrada())
                    .tipo(full.getEventoTipo().getNombre())
                    .direccion(full.getDireccion())
                    .imagen(full.getImagen())
                    .filaAsientos(full.getFilaAsientos())
                    .columnAsientos(full.getColumnAsientos())
                    .ultimaActualizacion(LocalDateTime.now().toString())
                    .build();

            eventoRepository.save(evento);
            count++;
        }

        return count;
    }
}
