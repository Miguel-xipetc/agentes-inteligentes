package Agentes;

import comportamientos.LimitarSubidasSeeder;
import comportamientos.ResponderSolicitudesSeeder;
import jade.core.Agent;

/** Posee las piezas originales y guarda el contador de subidas. */
public class AgenteSeeder extends Agent {
    private static final int MAX_SUBIDAS_POR_VENTANA = 4;
    private int subidasEnVentana = 0;

    @Override
    protected void setup() {
        System.out.println("[Seeder] Tengo el archivo completo y puedo compartirlo.");
        addBehaviour(new ResponderSolicitudesSeeder(this));
        addBehaviour(new LimitarSubidasSeeder(this));
    }

    public boolean limiteDeSubidasAlcanzado() {
        return subidasEnVentana >= MAX_SUBIDAS_POR_VENTANA;
    }

    public void registrarSubida() {
        subidasEnVentana++;
    }

    public void reiniciarSubidas() {
        subidasEnVentana = 0;
    }

    @Override
    protected void takeDown() {
        System.out.println("[Seeder] Finalizado.");
    }
}
