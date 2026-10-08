package Comun;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/** Operaciones comunes para crear mensajes ACL; no es un comportamiento. */
public final class MensajesP2P {
    private MensajesP2P() {
    }

    public static String contenido(ACLMessage mensaje) {
        return mensaje.getContent() == null ? "" : mensaje.getContent();
    }

    public static void registrarEnTracker(Agent agente, String rol) {
        solicitar(agente, new AID(ProtocoloP2P.TRACKER, AID.ISLOCALNAME),
            "REGISTER|" + rol, "registro-" + agente.getLocalName());
    }

    public static void solicitar(Agent agente, AID receptor, String contenido, String id) {
        ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
        mensaje.addReceiver(receptor);
        mensaje.setConversationId(id);
        mensaje.setContent(contenido);
        agente.send(mensaje);
    }

    public static void informarTracker(Agent agente, String contenido) {
        ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
        mensaje.addReceiver(new AID(ProtocoloP2P.TRACKER, AID.ISLOCALNAME));
        mensaje.setContent(contenido);
        agente.send(mensaje);
    }

    public static void responder(Agent agente, ACLMessage original,
            int performativa, String contenido) {
        ACLMessage respuesta = original.createReply();
        respuesta.setPerformative(performativa);
        respuesta.setContent(contenido);
        agente.send(respuesta);
    }
}
