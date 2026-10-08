package comportamientos;

import Agentes.AgentePeer;
import Comun.MensajesP2P;
import jade.core.behaviours.OneShotBehaviour;

/** Anuncia al Tracker que el Peer ya tiene el archivo completo. */
public class ConvertirseEnSeed extends OneShotBehaviour {
    private final AgentePeer peer;

    public ConvertirseEnSeed(AgentePeer peer) {
        super(peer);
        this.peer = peer;
    }

    @Override
    public void action() {
        MensajesP2P.informarTracker(peer, "HEARTBEAT|SEEDER");
        System.out.println("[Peer] Estado Seed. Archivo reconstruido: "
            + peer.getDatos().reconstruirArchivo());
    }
}
