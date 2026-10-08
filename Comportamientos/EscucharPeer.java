package comportamientos;

import Agentes.AgentePeer;
import Comun.DatosPeer;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/** Recibe respuestas de descarga y atiende solicitudes de otros peers. */
public class EscucharPeer extends CyclicBehaviour {
    private final AgentePeer peer;
    private final DatosPeer datos;

    public EscucharPeer(AgentePeer peer) {
        super(peer);
        this.peer = peer;
        this.datos = peer.getDatos();
    }

    @Override
    public void action() {
        ACLMessage mensaje = peer.receive();
        if (mensaje == null) {
            block();
            return;
        }
        String contenido = MensajesP2P.contenido(mensaje);
        if (mensaje.getPerformative() == ACLMessage.REQUEST
                && contenido.startsWith("PIECE|")) {
            atenderSolicitudDePieza(mensaje, contenido);
            return;
        }
        if (contenido.startsWith("SEEDERS|")) {
            recibirListaSeeders(mensaje, contenido);
            return;
        }
        if (!datos.esConversacionPendiente(mensaje.getConversationId())) {
            return;
        }
        if (datos.esperaPieza()) {
            recibirPieza(mensaje, contenido);
        } else if (datos.estaVerificandoPieza()) {
            recibirVerificacion(mensaje, contenido);
        }
    }

    private void recibirListaSeeders(ACLMessage mensaje, String contenido) {
        if (!datos.esBusquedaActual(mensaje.getConversationId())) {
            return;
        }
        String lista = contenido.substring("SEEDERS|".length());
        if (lista.isEmpty()) {
            // El Waker de búsqueda habilitará otro intento tras la pausa.
            return;
        }
        String nombre = lista.split(",")[0];
        datos.elegirSeeder(new AID(nombre, AID.ISLOCALNAME));
        System.out.println("[Peer] Encontré al seeder " + nombre + ".");
        peer.despertarControlador();
    }

    private void recibirPieza(ACLMessage mensaje, String contenido) {
        if (mensaje.getPerformative() != ACLMessage.INFORM) {
            peer.fallar("El Seeder rechazó la solicitud: " + contenido);
            return;
        }
        String[] partes = contenido.split("\\|", 3);
        if (partes.length != 3 || !"PIECE".equals(partes[0])) {
            peer.fallar("La respuesta del Seeder no tenía el formato esperado.");
            return;
        }
        try {
            int indice = Integer.parseInt(partes[1]);
            if (indice != datos.getSiguientePieza()) {
                peer.fallar("Recibí una pieza fuera de orden.");
                return;
            }
            datos.recibirPieza(indice, partes[2]);
            peer.despertarControlador();
        } catch (NumberFormatException e) {
            peer.fallar("El índice de pieza recibido no es válido.");
        }
    }

    private void recibirVerificacion(ACLMessage mensaje, String contenido) {
        if (mensaje.getPerformative() != ACLMessage.CONFIRM
                || !contenido.equals("VALID|" + datos.getSiguientePieza())) {
            peer.fallar("El Verificador no aceptó la pieza: " + contenido);
            return;
        }
        datos.guardarPiezaVerificada();
        MensajesP2P.informarTracker(peer, "PROGRESS|" + datos.getCantidadVerificada()
            + "|" + ProtocoloP2P.PIEZAS_ORIGINALES.length);
        System.out.println("[Peer] Pieza validada. Progreso: " + datos.getCantidadVerificada()
            + "/" + ProtocoloP2P.PIEZAS_ORIGINALES.length + ".");
        peer.despertarControlador();
    }

    private void atenderSolicitudDePieza(ACLMessage mensaje, String contenido) {
        try {
            int indice = Integer.parseInt(contenido.substring("PIECE|".length()));
            String pieza = datos.getPieza(indice);
            if (pieza == null) {
                MensajesP2P.responder(peer, mensaje, ACLMessage.REFUSE, "PIEZA_AUN_NO_DISPONIBLE");
            } else {
                MensajesP2P.responder(peer, mensaje, ACLMessage.INFORM,
                    "PIECE|" + indice + "|" + pieza);
                System.out.println("[Peer] Compartí pieza " + indice + " con "
                    + mensaje.getSender().getLocalName() + ".");
            }
        } catch (NumberFormatException e) {
            MensajesP2P.responder(peer, mensaje, ACLMessage.FAILURE, "INDICE_INVALIDO");
        }
    }
}
