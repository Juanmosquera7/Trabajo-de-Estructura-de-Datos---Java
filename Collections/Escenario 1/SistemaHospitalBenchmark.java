import java.util.*;

public class SistemaHospitalBenchmark {

    static class Paciente {
        String documento;
        String nombre;

        public Paciente(String documento, String nombre) {
            this.documento = documento;
            this.nombre = nombre;
        }
    }

    private final Map<String, Paciente> registroPacientes = new LinkedHashMap<>();

    public boolean registrarPaciente(String documento, String nombre) {
        if (registroPacientes.containsKey(documento)) {
            return false;
        }
        registroPacientes.put(documento, new Paciente(documento, nombre));
        return true;
    }

    public Paciente buscarPorDocumento(String documento) {
        return registroPacientes.get(documento);
    }

    public void limpiar() {
        registroPacientes.clear();
    }

    public static void main(String[] args) {
        int[] tamanos = {100, 1000, 10000, 100000};
        
        System.out.println("======================================================================");
        System.out.println("         BENCHMARK DE RENDIMIENTO: SISTEMA HOSPITAL (WONDERKIDS)      ");
        System.out.println("======================================================================");
        System.out.printf("%-12s | %-20s | %-20s | %-12s\n", "PACIENTES", "TIEMPO REGISTRO (ms)", "TIEMPO BUSQUEDA (ms)", "MEMORIA (MB)");
        System.out.println("----------------------------------------------------------------------");

        for (int n : tamanos) {
            SistemaHospitalBenchmark hospital = new SistemaHospitalBenchmark();
            
            // Forzar recolección de basura para limpiar pruebas anteriores
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

            // 1. Medir Tiempo de Registro (Inserción y control de duplicados)
            long inicioRegistro = System.nanoTime();
            for (int i = 0; i < n; i++) {
                hospital.registrarPaciente("DOC-" + i, "Wonderkid-" + i);
            }
            long finRegistro = System.nanoTime();
            double tiempoRegistroMs = (finRegistro - inicioRegistro) / 1_000_000.0;

            // 2. Medir Tiempo de Búsqueda de un elemento aleatorio (Peor caso / Promedio)
            String targetDoc = "DOC-" + (n - 1);
            long inicioBusqueda = System.nanoTime();
            hospital.buscarPorDocumento(targetDoc);
            long finBusqueda = System.nanoTime();
            double tiempoBusquedaMs = (finBusqueda - inicioBusqueda) / 1_000_000.0;

            // 3. Medir Uso de Memoria aproximado
            long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();
            double memoriaUtilizadaMb = (memoriaDespues - memoriaAntes) / (1024.0 * 1024.0);
            if (memoriaUtilizadaMb < 0) memoriaUtilizadaMb = 0.1; // Ajuste por fluctuación del GC

            System.out.printf("%-12d | %-20.4f | %-20.6f | %-12.2f\n", n, tiempoRegistroMs, tiempoBusquedaMs, memoriaUtilizadaMb);
            
            hospital.limpiar();
        }
        System.out.println("======================================================================");
    }
}
