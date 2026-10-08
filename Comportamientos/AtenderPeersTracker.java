package comportamientos;

import Agentes.AgenteTracker;
import Comun.MensajesP2P;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/** Escucha latidos y reportes de progreso. */
public class AtenderPeersTracker extends CyclicBehaviour {
    private final AgenteTracker tracker;
    private final MessageTemplate informes =
        MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    public AtenderPeersTracker(AgenteTracker tracker) {
        super(tracker);
        this.tracker = tracker;
    }

    @Override
    public void action() {
        ACLMessage mensaje = tracker.receive(informes);
        if (mensaje == null) {
            block();
            return;
        }

        String contenido = MensajesP2P.contenido(mensaje);
        String emisor = mensaje.getSender().getLocalName();
        if (contenido.startsWith("HEARTBEAT|")) {
            tracker.registrar(mensaje.getSender(), contenido.substring("HEARTBEAT|".length()));
        } else if (contenido.startsWith("PROGRESS|")) {
            String[] partes = contenido.split("\\|", 3);
            if (partes.length == 3) {
                System.out.println("[Tracker] " + emisor + " verificó "
                    + partes[1] + "/" + partes[2] + " piezas.");
            }
            tracker.actualizarLatido(emisor);
        }
    }
}
