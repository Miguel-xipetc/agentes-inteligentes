package comportamientos;

import Agentes.AgentePeer;
import Comun.MensajesP2P;
import jade.core.behaviours.OneShotBehaviour;

/** Envía un REQUEST al Seeder para una sola pieza. */
public class PedirPieza extends OneShotBehaviour {
    private final AgentePeer peer;
    private final int indice;
    private final String idConversacion;

    public PedirPieza(AgentePeer peer, int indice, String idConversacion) {
        super(peer);
        this.peer = peer;
        this.indice = indice;
        this.idConversacion = idConversacion;
    }

    @Override
    public void action() {
        if (!peer.getDatos().esConversacionPendiente(idConversacion)) {
            return;
        }
        MensajesP2P.solicitar(peer, peer.getDatos().getSeederElegido(),
            "PIECE|" + indice, idConversacion);
        System.out.println("[Peer] Solicito pieza " + indice + ".");
        peer.addBehaviour(new TimeoutRespuestaPeer(peer, idConversacion));
    }
}
