package comportamientos;

import Agentes.AgenteSeeder;
import Comun.MensajesP2P;
import Comun.ProtocoloP2P;
import jade.core.behaviours.TickerBehaviour;

/** Restablece la ventana de subidas y envía el latido del Seeder. */
public class LimitarSubidasSeeder extends TickerBehaviour {
    private final AgenteSeeder seeder;

    public LimitarSubidasSeeder(AgenteSeeder seeder) {
        super(seeder, ProtocoloP2P.INTERVALO_LATIDO_MS);
        this.seeder = seeder;
    }

    @Override
    protected void onTick() {
        seeder.reiniciarSubidas();
        MensajesP2P.informarTracker(seeder, "HEARTBEAT|SEEDER");
    }
}
