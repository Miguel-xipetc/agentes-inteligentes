package comportamientos;

import Agentes.AgenteSeeder;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/** Escucha REQUEST y sube una pieza si el límite de subidas lo permite. */
public class ResponderSolicitudesSeeder extends CyclicBehaviour {
    private final AgenteSeeder seeder;

    public ResponderSolicitudesSeeder(AgenteSeeder seeder) {
        super(seeder);
        this.seeder = seeder;
    }

    @Override
    public void onStart() {
        MensajesP2P.registrarEnTracker(seeder, "SEEDER");
    }

    @Override
    public void action() {
        ACLMessage mensaje = seeder.receive();
        if (mensaje == null) {
            block();
            return;
        }
        if (mensaje.getPerformative() != ACLMessage.REQUEST) {
            return;
        }

        String[] partes = MensajesP2P.contenido(mensaje).split("\\|", 2);
        if (partes.length != 2 || !"PIECE".equals(partes[0])) {
            MensajesP2P.responder(seeder, mensaje, ACLMessage.NOT_UNDERSTOOD, "Usa PIECE|indice");
            return;
        }

        try {
            int indice = Integer.parseInt(partes[1]);
            if (indice < 0 || indice >= ProtocoloP2P.PIEZAS_ORIGINALES.length) {
                MensajesP2P.responder(seeder, mensaje, ACLMessage.FAILURE, "INDICE_INVALIDO");
            } else if (seeder.limiteDeSubidasAlcanzado()) {
                MensajesP2P.responder(seeder, mensaje, ACLMessage.REFUSE, "LIMITE_DE_SUBIDAS");
            } else {
                seeder.registrarSubida();
                String pieza = "PIECE|" + indice + "|" + ProtocoloP2P.PIEZAS_ORIGINALES[indice];
                MensajesP2P.responder(seeder, mensaje, ACLMessage.INFORM, pieza);
                System.out.println("[Seeder] Envié pieza " + indice + " a "
                    + mensaje.getSender().getLocalName() + ".");
            }
        } catch (NumberFormatException e) {
            MensajesP2P.responder(seeder, mensaje, ACLMessage.FAILURE, "INDICE_INVALIDO");
        }
    }
}
