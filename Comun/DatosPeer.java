package Comun;

import jade.core.AID;

import java.util.HashMap;
import java.util.Map;

/**
 * Estado compartido por los comportamientos del Peer.
 * Los comportamientos del mismo agente se ejecutan de forma cooperativa.
 */
public class DatosPeer {
    public enum Estado { INTERESADO, DESCARGANDO, SEED, FALLO }

    private Estado estado = Estado.INTERESADO;
    private final Map<Integer, String> piezasVerificadas = new HashMap<>();
    private AID seederElegido;
    private boolean busquedaPendiente;
    private String idBusquedaActual;
    private int contadorConversaciones;
    private int siguientePieza;
    private int indicePiezaRecibida = -1;
    private String contenidoPiezaRecibida;
    private String contenidoEnVerificacion;
    private boolean esperaPieza;
    private boolean verificandoPieza;
    private String conversacionPendiente;
    private boolean anunciadoComoSeeder;
    private boolean falloReportado;

    public Estado getEstado() {
        return estado;
    }

    public AID getSeederElegido() {
        return seederElegido;
    }

    public boolean hayBusquedaPendiente() {
        return busquedaPendiente;
    }

    public String iniciarBusqueda() {
        busquedaPendiente = true;
        idBusquedaActual = nuevoId("buscar");
        return idBusquedaActual;
    }

    public boolean esBusquedaActual(String id) {
        return estado == Estado.INTERESADO && id != null && id.equals(idBusquedaActual);
    }

    public boolean reintentarBusqueda(String id) {
        if (busquedaPendiente && esBusquedaActual(id)) {
            busquedaPendiente = false;
            return true;
        }
        return false;
    }

    public void elegirSeeder(AID seeder) {
        seederElegido = seeder;
        busquedaPendiente = false;
        estado = Estado.DESCARGANDO;
    }

    public int getSiguientePieza() {
        return siguientePieza;
    }

    public boolean esperaPieza() {
        return esperaPieza;
    }

    public boolean estaVerificandoPieza() {
        return verificandoPieza;
    }

    public String iniciarSolicitudPieza() {
        esperaPieza = true;
        conversacionPendiente = nuevoId("pieza-" + siguientePieza);
        return conversacionPendiente;
    }

    public boolean esConversacionPendiente(String id) {
        return conversacionPendiente != null && conversacionPendiente.equals(id);
    }

    public void recibirPieza(int indice, String contenido) {
        esperaPieza = false;
        conversacionPendiente = null;
        indicePiezaRecibida = indice;
        contenidoPiezaRecibida = contenido;
    }

    public boolean hayPiezaRecibida() {
        return indicePiezaRecibida >= 0;
    }

    public int getIndicePiezaRecibida() {
        return indicePiezaRecibida;
    }

    public String getContenidoPiezaRecibida() {
        return contenidoPiezaRecibida;
    }

    public String iniciarVerificacion() {
        contenidoEnVerificacion = contenidoPiezaRecibida;
        verificandoPieza = true;
        conversacionPendiente = nuevoId("verificar-" + indicePiezaRecibida);
        indicePiezaRecibida = -1;
        contenidoPiezaRecibida = null;
        return conversacionPendiente;
    }

    public void guardarPiezaVerificada() {
        piezasVerificadas.put(siguientePieza, contenidoEnVerificacion);
        siguientePieza++;
        verificandoPieza = false;
        conversacionPendiente = null;
        contenidoEnVerificacion = null;
        if (descargaCompleta()) {
            estado = Estado.SEED;
        }
    }

    public String getPieza(int indice) {
        return piezasVerificadas.get(indice);
    }

    public int getCantidadVerificada() {
        return piezasVerificadas.size();
    }

    public boolean descargaCompleta() {
        return piezasVerificadas.size() == ProtocoloP2P.PIEZAS_ORIGINALES.length;
    }

    public boolean marcarAnuncioSeeder() {
        if (anunciadoComoSeeder) {
            return false;
        }
        anunciadoComoSeeder = true;
        return true;
    }

    public boolean marcarReporteFallo() {
        if (falloReportado) {
            return false;
        }
        falloReportado = true;
        return true;
    }

    public boolean marcarFallo() {
        if (estado == Estado.FALLO || descargaCompleta()) {
            return false;
        }
        estado = Estado.FALLO;
        esperaPieza = false;
        verificandoPieza = false;
        conversacionPendiente = null;
        return true;
    }

    public String reconstruirArchivo() {
        StringBuilder archivo = new StringBuilder();
        for (int i = 0; i < ProtocoloP2P.PIEZAS_ORIGINALES.length; i++) {
            archivo.append(piezasVerificadas.get(i));
        }
        return archivo.toString();
    }

    private String nuevoId(String prefijo) {
        return prefijo + "-" + (++contadorConversaciones);
    }
}
