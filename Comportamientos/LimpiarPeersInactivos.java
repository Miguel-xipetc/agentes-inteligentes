package comportamientos;

import Agentes.AgenteTracker;
import jade.core.behaviours.TickerBehaviour;

/** Revisa periódicamente qué participantes dejaron de enviar latidos. */
public class LimpiarPeersInactivos extends TickerBehaviour {
    private final AgenteTracker tracker;

    public LimpiarPeersInactivos(AgenteTracker tracker) {
        super(tracker, 5000);
        this.tracker = tracker;
    }

    @Override
    protected void onTick() {
        tracker.limpiarPeersInactivos();
    }
}
