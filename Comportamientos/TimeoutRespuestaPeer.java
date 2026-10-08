package comportamientos;

import Agentes.AgentePeer;
import Comun.DatosPeer;
import Comun.ProtocoloP2P;
import jade.core.behaviours.WakerBehaviour;

/** Un único tipo de Waker para búsqueda, respuestas y tiempo total de descarga. */
public class TimeoutRespuestaPeer extends WakerBehaviour {
    private enum Tipo { BUSQUEDA, RESPUESTA, DESCARGA }

    private final AgentePeer peer;
    private final String idConversacion;
    private final Tipo tipo;

    public TimeoutRespuestaPeer(AgentePeer peer, String idConversacion) {
        this(peer, idConversacion, Tipo.RESPUESTA, ProtocoloP2P.ESPERA_RESPUESTA_MS);
    }

    /** Constructor para el límite total de la descarga. */
    public TimeoutRespuestaPeer(AgentePeer peer) {
        this(peer, null, Tipo.DESCARGA, ProtocoloP2P.TIEMPO_DESCARGA_MS);
    }

    /** Crea otra instancia de esta misma clase para reintentar la búsqueda. */
    public static TimeoutRespuestaPeer paraBusqueda(AgentePeer peer, String idConversacion) {
        return new TimeoutRespuestaPeer(peer, idConversacion,
            Tipo.BUSQUEDA, ProtocoloP2P.INTERVALO_REINTENTO_MS);
    }

    private TimeoutRespuestaPeer(AgentePeer peer, String idConversacion,
            Tipo tipo, long tiempoMs) {
        super(peer, tiempoMs);
        this.peer = peer;
        this.idConversacion = idConversacion;
        this.tipo = tipo;
    }

    @Override
    protected void onWake() {
        DatosPeer datos = peer.getDatos();
        switch (tipo) {
            case BUSQUEDA:
                if (datos.reintentarBusqueda(idConversacion)) {
                    peer.despertarControlador();
                }
                break;
            case RESPUESTA:
                if (datos.esConversacionPendiente(idConversacion)) {
                    peer.fallar("No llegó respuesta para " + idConversacion + ".");
                }
                break;
            case DESCARGA:
                if (!datos.descargaCompleta() && datos.getEstado() != DatosPeer.Estado.FALLO) {
                    peer.fallar("Se agotó el tiempo para completar la descarga.");
                }
                break;
            default:
                break;
        }
    }
}
