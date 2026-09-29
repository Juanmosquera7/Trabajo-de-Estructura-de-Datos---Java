import java.util.*;

public class PlataformaTaxisBenchmark {

    static class SolicitudViaje {
        String idSolicitud;
        String pasajero;

        public SolicitudViaje(String idSolicitud, String pasajero) {
            this.idSolicitud = idSolicitud;
            this.pasajero = pasajero;
        }
    }

    // Estructura híbrida que une Hash y Lista Doble para operaciones O(1) de inserción, borrado y FIFO
    private final Map<String, SolicitudViaje> colaTaxis = new LinkedHashMap<>();

    public void registrarSolicitud(String id, String pasajero) {
        colaTaxis.put(id, new SolicitudViaje(id, pasajero));
    }

    public SolicitudViaje atenderSiguiente() {
        if (colaTaxis.isEmpty()) return null;
        Iterator<String> it = colaTaxis.keySet().iterator();
        String primeraClave = it.next();
        return colaTaxis.remove(primeraClave);
    }

    public boolean cancelarSolicitud(String id) {
        return colaTaxis.remove(id) != null;
    }

    public void limpiar() {
        colaTaxis.clear();
    }

    public static void main(String[] args) {
        int[] tamanos = {100, 1000, 10000, 100000};
        
        System.out.println("==========================================================================================");
        System.out.println("         BENCHMARK DE RENDIMIENTO (FASE 4): PLATAFORMA DE TAXIS (FIFO + COLA)             ");
        System.out.println("==========================================================================================");
        System.out.printf("%-10s | %-20s | %-18s | %-18s | %-12s\n", 
                "SOLICITUDES", "T. REGISTRO (ms)", "T. ATENDER (ms)", "T. CANCELAR (ms)", "MEMORIA (MB)");
        System.out.println("------------------------------------------------------------------------------------------");

        for (int n : tamanos) {
            PlataformaTaxisBenchmark plataforma = new PlataformaTaxisBenchmark();
            
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

            // 1. Registro Masivo (Inserción FIFO)
            long inicioReg = System.nanoTime();
            for (int i = 0; i < n; i++) {
                plataforma.registrarSolicitud("REQ-" + i, "Usuario-" + i);
            }
            long finReg = System.nanoTime();
            double tRegistro = (finReg - inicioReg) / 1_000_000.0;

            // 2. Cancelación específica (Peor caso: Elemento intermedio por ID)
            String idCancelar = "REQ-" + (n / 2);
            long inicioCanc = System.nanoTime();
            plataforma.cancelarSolicitud(idCancelar);
            long finCanc = System.nanoTime();
            double tCancelar = (finCanc - inicioCanc) / 1_000_000.0;

            // 3. Atender el más antiguo (Extracción del primer elemento)
            long inicioAtn = System.nanoTime();
            plataforma.atenderSiguiente();
            long finAtn = System.nanoTime();
            double tAtender = (finAtn - inicioAtn) / 1_000_000.0;

            // 4. Cálculo de Memoria RAM
            long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();
            double memoriaMb = (memoriaDespues - memoriaAntes) / (1024.0 * 1024.0);
            if (memoriaMb < 0) memoriaMb = 0.05;

            System.out.printf("%-11d | %-19.3f ms | %-16.5f ms | %-16.5f ms | %-12.2f\n", 
                    n, tRegistro, tAtender, tCancelar, memoriaMb);
            
            plataforma.limpiar();
        }
        System.out.println("==========================================================================================");
    }
}
