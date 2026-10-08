package comportamientos;

import Agentes.AgenteTracker;
import Comun.MensajesP2P;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/** Envía una sola respuesta al registro o a la búsqueda de seeders. */
public class ConfirmarAsignacion extends OneShotBehaviour {
    private final ACLMessage solicitud;
    private final int performativa;
    private final String contenido;

    public ConfirmarAsignacion(AgenteTracker tracker, ACLMessage solicitud,
            int performativa, String contenido) {
        super(tracker);
        this.solicitud = solicitud;
        this.performativa = performativa;
        this.contenido = contenido;
    }

    @Override
    public void action() {
        MensajesP2P.responder(myAgent, solicitud, performativa, contenido);
        System.out.println("[Tracker] Respondí a " + solicitud.getSender().getLocalName()
            + " con " + ACLMessage.getPerformative(performativa) + ".");
    }
}
