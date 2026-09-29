import java.util.*;

public class ClinicaFutbolistas {

    // Representa a la estrella de fútbol que ingresa a la clínica
    static class Futbolista {
        String pasaporte; // Actúa como el número de documento único
        String nombre;
        String club;

        public Futbolista(String pasaporte, String nombre, String club) {
            this.pasaporte = pasaporte;
            this.nombre = nombre;
            this.club = club;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Futbolista)) return false;
            Futbolista futbolista = (Futbolista) o;
            return Objects.equals(pasaporte, futbolista.pasaporte);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pasaporte);
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (%s)", pasaporte, nombre, club);
        }
    }

    // Estructura híbrida que garantiza orden de llegada y búsqueda O(1) por pasaporte
    private final Map<String, Futbolista> salaEspera = new LinkedHashMap<>();

    // 1. Registrar futbolista al llegar (Evita duplicados en la sala de espera)
    public boolean ingresarFutbolista(String pasaporte, String nombre, String club) {
        if (salaEspera.containsKey(pasaporte)) {
            System.out.println("[ERROR] Alerta Medica: " + nombre + " ya esta registrado en la sala de espera.");
            return false;
        }
        Futbolista crack = new Futbolista(pasaporte, nombre, club);
        salaEspera.put(pasaporte, crack);
        System.out.println("[OK] Registrado en camilla: " + crack.nombre + " [" + club + "]");
        return true;
    }

    // 2. Buscar rápidamente por número de pasaporte/documento en O(1)
    public Futbolista buscarPorPasaporte(String pasaporte) {
        return salaEspera.get(pasaporte);
    }

    // 3. Mantener y mostrar el orden estricto de llegada
    public void mostrarFilaDeAtencion() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("FILA DE ATENCION MEDICA (Orden de Llegada)");
        System.out.println("--------------------------------------------------");
        for (Futbolista f : salaEspera.values()) {
            System.out.println(f);
        }
        System.out.println("--------------------------------------------------");
    }

    // Simulación del centro médico con los jugadores
    public static void main(String[] args) {
        ClinicaFutbolistas centroMedico = new ClinicaFutbolistas();

        System.out.println("=== LLEGADA DE JUGADORES LESIONADOS ===");
        // Registros en orden cronológico de llegada
        centroMedico.ingresarFutbolista("PAS-10", "Lionel Messi", "Inter Miami");
        centroMedico.ingresarFutbolista("PAS-07", "Cristiano Ronaldo", "Al-Nassr");
        centroMedico.ingresarFutbolista("PAS-09", "Kylian Mbappe", "Real Madrid");

        System.out.println("\n=== INTENTO DE REGISTRO DUPLICADO ===");
        // Intento de ingresar un pasaporte duplicado
        centroMedico.ingresarFutbolista("PAS-07", "Cristiano Ronaldo", "Al-Nassr"); 

        System.out.println("\n=== BUSQUEDA DEL REPORTE MEDICO O(1) ===");
        String buscarID = "PAS-10";
        Futbolista paciente = centroMedico.buscarPorPasaporte(buscarID);
        System.out.println("Buscando pasaporte " + buscarID + " -> Encontrado: " + (paciente != null ? paciente.nombre : "Ninguno"));

        // Se imprime la lista respetando quién llegó primero
        centroMedico.mostrarFilaDeAtencion();
    }
}
