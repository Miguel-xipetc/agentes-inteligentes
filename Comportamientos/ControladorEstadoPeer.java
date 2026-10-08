package comportamientos;

import Agentes.AgentePeer;
import Comun.DatosPeer;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

/** Controla el estado, busca seeders y mantiene el registro activo. */
public class ControladorEstadoPeer extends Behaviour {
    private final AgentePeer peer;
    private long proximoLatido;

    public ControladorEstadoPeer(AgentePeer peer) {
        super(peer);
        this.peer = peer;
    }

    @Override
    public void onStart() {
        MensajesP2P.registrarEnTracker(peer, "LEECHER");
        proximoLatido = System.currentTimeMillis() + ProtocoloP2P.INTERVALO_LATIDO_MS;
    }

    @Override
    public void action() {
        DatosPeer datos = peer.getDatos();
        enviarLatidoSiCorresponde(datos);
        switch (datos.getEstado()) {
            case INTERESADO:
                if (!datos.hayBusquedaPendiente()) {
                    String id = datos.iniciarBusqueda();
                    MensajesP2P.solicitar(peer,
                        new AID(ProtocoloP2P.TRACKER, AID.ISLOCALNAME), "GET_SEEDERS", id);
                    peer.addBehaviour(TimeoutRespuestaPeer.paraBusqueda(peer, id));
                }
                break;

            case DESCARGANDO:
                if (datos.hayPiezaRecibida() && !datos.estaVerificandoPieza()) {
                    int indice = datos.getIndicePiezaRecibida();
                    String contenido = datos.getContenidoPiezaRecibida();
                    String id = datos.iniciarVerificacion();
                    peer.addBehaviour(new ProcesarPiezaRecibida(peer, indice, contenido, id));
                } else if (!datos.esperaPieza() && !datos.estaVerificandoPieza()) {
                    int indice = datos.getSiguientePieza();
                    String id = datos.iniciarSolicitudPieza();
                    peer.addBehaviour(new PedirPieza(peer, indice, id));
                }
                break;

            case SEED:
                if (datos.marcarAnuncioSeeder()) {
                    peer.addBehaviour(new ConvertirseEnSeed(peer));
                }
                break;

            case FALLO:
                if (datos.marcarReporteFallo()) {
                    System.out.println("[Peer] Descarga detenida. Revisa los agentes "
                        + "activos y los mensajes de error.");
                }
                break;

            default:
                break;
        }
        if (datos.getEstado() == DatosPeer.Estado.FALLO) {
            block();
        } else {
            // La pausa con tiempo permite enviar latidos aunque no lleguen mensajes.
            block(Math.max(1, proximoLatido - System.currentTimeMillis()));
        }
    }

    private void enviarLatidoSiCorresponde(DatosPeer datos) {
        long ahora = System.currentTimeMillis();
        if (datos.getEstado() != DatosPeer.Estado.FALLO && ahora >= proximoLatido) {
            MensajesP2P.informarTracker(peer, "HEARTBEAT|"
                + (datos.descargaCompleta() ? "SEEDER" : "LEECHER"));
            proximoLatido = ahora + ProtocoloP2P.INTERVALO_LATIDO_MS;
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
