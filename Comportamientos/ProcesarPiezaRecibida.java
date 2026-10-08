package comportamientos;

import Agentes.AgentePeer;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/** Envía al Verificador una pieza recibida antes de permitir que se comparta. */
public class ProcesarPiezaRecibida extends OneShotBehaviour {
    private final AgentePeer peer;
    private final int indice;
    private final String contenido;
    private final String idConversacion;

    public ProcesarPiezaRecibida(AgentePeer peer, int indice,
            String contenido, String idConversacion) {
        super(peer);
        this.peer = peer;
        this.indice = indice;
        this.contenido = contenido;
        this.idConversacion = idConversacion;
    }

    @Override
    public void action() {
        if (!peer.getDatos().esConversacionPendiente(idConversacion)) {
            return;
        }
        MensajesP2P.solicitar(peer, new AID(ProtocoloP2P.VERIFICADOR, AID.ISLOCALNAME),
            "VERIFY|" + indice + "|" + contenido, idConversacion);
        System.out.println("[Peer] Envié pieza " + indice + " al Verificador.");
        peer.addBehaviour(new TimeoutRespuestaPeer(peer, idConversacion));
    }
}
