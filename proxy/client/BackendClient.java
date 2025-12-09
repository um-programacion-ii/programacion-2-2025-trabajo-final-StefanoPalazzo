package com.stefanopalazzo.proxy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "backend", url = "http://localhost:8081")
public interface BackendClient {

    @GetMapping("/api/eventos")
    Object getEventos();

    @GetMapping("/api/eventos/{id}")
    Object getEvento(@PathVariable Integer id);
}
