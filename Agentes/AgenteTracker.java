package Agentes;

import Comun.ProtocoloP2P;
import comportamientos.AtenderPeersTracker;
import comportamientos.ControladorEstadoTracker;
import comportamientos.LimpiarPeersInactivos;
import jade.core.AID;
import jade.core.Agent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Guarda el registro de participantes y agrega las tareas del Tracker. */
public class AgenteTracker extends Agent {
    private final Map<String, PeerRegistrado> peers = new LinkedHashMap<>();

    @Override
    protected void setup() {
        System.out.println("[Tracker] Plataforma P2P lista.");
        addBehaviour(new AtenderPeersTracker(this));
        addBehaviour(new ControladorEstadoTracker(this));
        addBehaviour(new LimpiarPeersInactivos(this));
    }

    public void registrar(AID aid, String rol) {
        PeerRegistrado peer = peers.get(aid.getLocalName());
        if (peer == null) {
            peers.put(aid.getLocalName(), new PeerRegistrado(aid, rol));
            System.out.println("[Tracker] Registré " + aid.getLocalName() + " como " + rol + ".");
        } else {
            peer.rol = rol;
            peer.ultimoLatido = System.currentTimeMillis();
        }
    }

    public String crearListaSeeders() {
        StringBuilder lista = new StringBuilder("SEEDERS|");
        for (PeerRegistrado peer : peers.values()) {
            if ("SEEDER".equals(peer.rol)) {
                if (lista.length() > "SEEDERS|".length()) {
                    lista.append(',');
                }
                lista.append(peer.aid.getLocalName());
            }
        }
        return lista.toString();
    }

    public void actualizarLatido(String nombre) {
        PeerRegistrado peer = peers.get(nombre);
        if (peer != null) {
            peer.ultimoLatido = System.currentTimeMillis();
        }
    }

    public void limpiarPeersInactivos() {
        long ahora = System.currentTimeMillis();
        Iterator<Map.Entry<String, PeerRegistrado>> it = peers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, PeerRegistrado> entrada = it.next();
            if (ahora - entrada.getValue().ultimoLatido > ProtocoloP2P.TIEMPO_INACTIVO_MS) {
                System.out.println("[Tracker] Eliminé por inactividad a " + entrada.getKey() + ".");
                it.remove();
            }
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("[Tracker] Finalizado.");
    }

    /** Datos de un participante; esta clase no es un comportamiento. */
    private static class PeerRegistrado {
        private final AID aid;
        private String rol;
        private long ultimoLatido;

        private PeerRegistrado(AID aid, String rol) {
            this.aid = aid;
            this.rol = rol;
            this.ultimoLatido = System.currentTimeMillis();
        }
    }
}
