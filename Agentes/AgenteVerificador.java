package Agentes;

import comportamientos.RecibirPiezasVerificador;
import jade.core.Agent;

/** Agrega la tarea que comprueba los hashes de las piezas recibidas. */
public class AgenteVerificador extends Agent {
    @Override
    protected void setup() {
        System.out.println("[Verificador] Listo para comprobar piezas con SHA-256.");
        addBehaviour(new RecibirPiezasVerificador(this));
    }
}
