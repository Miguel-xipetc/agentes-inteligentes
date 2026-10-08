package Comun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Datos y reglas compartidos por los agentes de la demostración.
 * El archivo se representa con tres piezas pequeñas para que el ejemplo
 * se pueda ejecutar en clase sin depender de archivos externos.
 */
public final class ProtocoloP2P {
    public static final String TRACKER = "tracker";
    public static final String VERIFICADOR = "verificador";

    public static final long ESPERA_RESPUESTA_MS = 5000;
    public static final long TIEMPO_DESCARGA_MS = 60000;
    public static final long INTERVALO_REINTENTO_MS = 1500;
    public static final long INTERVALO_LATIDO_MS = 5000;
    public static final long TIEMPO_INACTIVO_MS = 20000;

    public static final String[] PIEZAS_ORIGINALES = {
        "Hola ", "desde ", "JADE!"
    };

    private static final String[] HASHES_ORIGINALES = crearHashes();

    private ProtocoloP2P() {
    }

    public static String hash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte b : bytes) {
                resultado.append(String.format("%02x", b & 0xff));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Java no encontró SHA-256", e);
        }
    }

    public static String hashOriginal(int indice) {
        if (indice < 0 || indice >= HASHES_ORIGINALES.length) {
            return null;
        }
        return HASHES_ORIGINALES[indice];
    }

    private static String[] crearHashes() {
        String[] hashes = new String[PIEZAS_ORIGINALES.length];
        for (int i = 0; i < PIEZAS_ORIGINALES.length; i++) {
            hashes[i] = hash(PIEZAS_ORIGINALES[i]);
        }
        return hashes;
    }
}
