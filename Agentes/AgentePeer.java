package Agentes;

import Comun.DatosPeer;
import comportamientos.ControladorEstadoPeer;
import comportamientos.EscucharPeer;
import comportamientos.TimeoutRespuestaPeer;
import jade.core.Agent;

/** Guarda el estado del Peer y agrega sus comportamientos. */
public class AgentePeer extends Agent {
    private final DatosPeer datos = new DatosPeer();
    private ControladorEstadoPeer controlador;

    @Override
    protected void setup() {
        System.out.println("[Peer] Inicio como leecher; me registro en el Tracker.");

        controlador = new ControladorEstadoPeer(this);
        addBehaviour(new EscucharPeer(this));
        addBehaviour(controlador);
        addBehaviour(new TimeoutRespuestaPeer(this));
    }

    public DatosPeer getDatos() {
        return datos;
    }

    public void despertarControlador() {
        controlador.restart();
    }

    public void fallar(String motivo) {
        if (datos.marcarFallo()) {
            System.out.println("[Peer] Error: " + motivo);
            despertarControlador();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("[Peer] Finalizado.");
    }
}
