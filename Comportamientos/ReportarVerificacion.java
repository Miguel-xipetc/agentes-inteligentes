package comportamientos;

import Agentes.AgenteVerificador;
import Comun.MensajesP2P;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/** Reporta CONFIRM para una pieza válida o FAILURE para una pieza rechazada. */
public class ReportarVerificacion extends OneShotBehaviour {
    private final ACLMessage solicitud;
    private final int performativa;
    private final String contenido;

    public ReportarVerificacion(AgenteVerificador verificador, ACLMessage solicitud,
            int performativa, String contenido) {
        super(verificador);
        this.solicitud = solicitud;
        this.performativa = performativa;
        this.contenido = contenido;
    }

    @Override
    public void action() {
        MensajesP2P.responder(myAgent, solicitud, performativa, contenido);
        System.out.println("[Verificador] " + contenido + " ("
            + ACLMessage.getPerformative(performativa) + ").");
    }
}
