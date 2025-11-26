package com.stefanopalazzo.eventosbackend.carrito;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public Carrito ver() {
        return carritoService.getCarrito();
    }

    @PostMapping("/agregar")
    public Carrito agregar(@RequestBody CarritoItem item) {
        carritoService.agregarItem(item);
        return carritoService.getCarrito();
    }

    @DeleteMapping("/quitar")
    public Carrito quitar(@RequestBody CarritoItem item) {
        carritoService.quitarItem(item);
        return carritoService.getCarrito();
    }

    @DeleteMapping("/limpiar")
    public String limpiar() {
        carritoService.limpiar();
        return "Carrito vacío";
    }
}
