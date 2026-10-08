package comportamientos;

import Agentes.AgenteVerificador;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/** Comprueba el hash de una pieza y programa el reporte de resultado. */
public class RecibirPiezasVerificador extends CyclicBehaviour {
    private final AgenteVerificador verificador;

    public RecibirPiezasVerificador(AgenteVerificador verificador) {
        super(verificador);
        this.verificador = verificador;
    }

    @Override
    public void action() {
        ACLMessage mensaje = verificador.receive();
        if (mensaje == null) {
            block();
            return;
        }
        String[] partes = MensajesP2P.contenido(mensaje).split("\\|", 3);
        if (mensaje.getPerformative() != ACLMessage.REQUEST
                || partes.length != 3 || !"VERIFY".equals(partes[0])) {
            reportar(mensaje, ACLMessage.NOT_UNDERSTOOD, "Usa VERIFY|indice|contenido");
            return;
        }

        try {
            int indice = Integer.parseInt(partes[1]);
            String esperado = ProtocoloP2P.hashOriginal(indice);
            boolean valido = esperado != null
                && esperado.equals(ProtocoloP2P.hash(partes[2]));
            if (valido) {
                reportar(mensaje, ACLMessage.CONFIRM, "VALID|" + indice);
            } else {
                reportar(mensaje, ACLMessage.FAILURE, "INVALID|" + indice);
            }
        } catch (NumberFormatException e) {
            reportar(mensaje, ACLMessage.FAILURE, "INVALID|INDICE");
        }
    }

    private void reportar(ACLMessage mensaje, int performativa, String contenido) {
        verificador.addBehaviour(new ReportarVerificacion(
            verificador, mensaje, performativa, contenido));
    }
}
