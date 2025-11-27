package com.stefanopalazzo.eventosbackend.carrito;

import com.stefanopalazzo.eventosbackend.catedra.dto.QuitarItemDto;
import com.stefanopalazzo.eventosbackend.venta.Venta;
import com.stefanopalazzo.eventosbackend.venta.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final VentaService ventaService;

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
    public Carrito quitar(@RequestBody QuitarItemDto req) {

        carritoService.quitarItem(req.getEventoId(), req.getFila(), req.getColumna());

        return carritoService.getCarrito();
    }


    @DeleteMapping("/limpiar")
    public String limpiar() {
        carritoService.limpiar();
        return "Carrito vacío";
    }

    /** FINALIZAR COMPRA */
    @PostMapping("/finalizar/{eventoId}")
    public Venta finalizar(@PathVariable int eventoId) throws Exception {
        return ventaService.confirmarVenta(eventoId);
    }
}
