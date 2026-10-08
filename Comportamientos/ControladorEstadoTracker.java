package comportamientos;

import Agentes.AgenteTracker;
import Comun.MensajesP2P;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/** Controlador propio: Esperando -> Asignando -> Confirmando. */
public class ControladorEstadoTracker extends Behaviour {
    private enum Estado { ESPERANDO, ASIGNANDO, CONFIRMANDO }

    private final AgenteTracker tracker;
    private final MessageTemplate solicitudes =
        MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
    private Estado estado = Estado.ESPERANDO;
    private ACLMessage solicitudPendiente;
    private int performativaRespuesta;
    private String contenidoRespuesta;

    public ControladorEstadoTracker(AgenteTracker tracker) {
        super(tracker);
        this.tracker = tracker;
    }

    @Override
    public void action() {
        switch (estado) {
            case ESPERANDO:
                ACLMessage solicitud = tracker.receive(solicitudes);
                if (solicitud == null) {
                    block();
                    return;
                }
                solicitudPendiente = solicitud;
                String contenido = MensajesP2P.contenido(solicitud);
                if (contenido.startsWith("REGISTER|")) {
                    String rol = contenido.substring("REGISTER|".length());
                    tracker.registrar(solicitud.getSender(), rol);
                    performativaRespuesta = ACLMessage.CONFIRM;
                    contenidoRespuesta = "REGISTERED|" + rol;
                    estado = Estado.CONFIRMANDO;
                } else if (contenido.equals("GET_SEEDERS")) {
                    estado = Estado.ASIGNANDO;
                } else {
                    performativaRespuesta = ACLMessage.REFUSE;
                    contenidoRespuesta = "SOLICITUD_NO_RECONOCIDA";
                    estado = Estado.CONFIRMANDO;
                }
                break;

            case ASIGNANDO:
                contenidoRespuesta = tracker.crearListaSeeders();
                performativaRespuesta = ACLMessage.INFORM;
                estado = Estado.CONFIRMANDO;
                break;

            case CONFIRMANDO:
                tracker.addBehaviour(new ConfirmarAsignacion(tracker, solicitudPendiente,
                    performativaRespuesta, contenidoRespuesta));
                solicitudPendiente = null;
                contenidoRespuesta = null;
                estado = Estado.ESPERANDO;
                break;

            default:
                block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
