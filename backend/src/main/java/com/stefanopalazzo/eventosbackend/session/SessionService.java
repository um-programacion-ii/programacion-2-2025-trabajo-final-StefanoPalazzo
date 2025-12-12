package com.stefanopalazzo.eventosbackend.session;

import com.stefanopalazzo.eventosbackend.carrito.CarritoItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {

    private static final String CARRITO_KEY = "carrito";
    private static final String EVENTO_SELECCIONADO_KEY = "eventoSeleccionado";
    private static final String PASO_FLUJO_KEY = "pasoFlujo";

    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    @SuppressWarnings("unchecked")
    public List<CarritoItem> getCarrito() {
        List<CarritoItem> carrito = (List<CarritoItem>) getSession().getAttribute(CARRITO_KEY);
        if (carrito == null) {
            carrito = new ArrayList<>();
            getSession().setAttribute(CARRITO_KEY, carrito);
        }
        return carrito;
    }

    public void setCarrito(List<CarritoItem> carrito) {
        getSession().setAttribute(CARRITO_KEY, carrito);
    }

    public void limpiarCarrito() {
        getSession().setAttribute(CARRITO_KEY, new ArrayList<CarritoItem>());
    }

    public Integer getEventoSeleccionado() {
        return (Integer) getSession().getAttribute(EVENTO_SELECCIONADO_KEY);
    }

    public void setEventoSeleccionado(Integer eventoId) {
        getSession().setAttribute(EVENTO_SELECCIONADO_KEY, eventoId);
    }

    public String getPasoFlujo() {
        String paso = (String) getSession().getAttribute(PASO_FLUJO_KEY);
        return paso != null ? paso : "LISTA_EVENTOS";
    }

    public void setPasoFlujo(String paso) {
        getSession().setAttribute(PASO_FLUJO_KEY, paso);
    }

    public void invalidarSesion() {
        getSession().invalidate();
    }
}
