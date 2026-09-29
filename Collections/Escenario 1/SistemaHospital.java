import java.util.*;

public class SistemaHospital {

    // Clase interna para representar al Paciente
    static class Paciente {
        String documento;
        String nombre;
        long timestampRegistro;

        public Paciente(String documento, String nombre) {
            this.documento = documento;
            this.nombre = nombre;
            this.timestampRegistro = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Paciente)) return false;
            Paciente paciente = (Paciente) o;
            return Objects.equals(documento, paciente.documento);
        }

        @Override
        public int hashCode() {
            return Objects.hash(documento);
        }

        @Override
        public String toString() {
            return String.format("Paciente[Doc: %s, Nombre: %s]", documento, nombre);
        }
    }

    // Estructura principal: Mantiene orden de inserción y permite búsquedas O(1)
    private final Map<String, Paciente> registroPacientes = new LinkedHashMap<>();

    // 1. Registrar paciente evitando duplicados y manteniendo el orden
    public boolean registrarPaciente(String documento, String nombre) {
        if (registroPacientes.containsKey(documento)) {
            System.out.println("[ERROR] El paciente con documento " + documento + " ya esta registrado.");
            return false;
        }
        Paciente nuevo = new Paciente(documento, nombre);
        registroPacientes.put(documento, nuevo);
        System.out.println("[OK] Registrado con exito: " + nuevo.nombre);
        return true;
    }

    // 2. Buscar rápidamente por número de documento en O(1)
    public Paciente buscarPorDocumento(String documento) {
        return registroPacientes.get(documento);
    }

    // 3. Mostrar el orden de llegada
    public void mostrarOrdenDeLlegada() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("COLA DE ATENCION (Orden de llegada)");
        System.out.println("--------------------------------------------------");
        for (Paciente p : registroPacientes.values()) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------------------");
    }

    // Método principal para pruebas
    public static void main(String[] args) {
        SistemaHospital hospital = new SistemaHospital();

        System.out.println("=== 1. Registrando Pacientes ===");
        hospital.registrarPaciente("1919", "Lamine Yamal");
        hospital.registrarPaciente("1616", "Endrick");
        hospital.registrarPaciente("1515", "Arda Guler");

        System.out.println("\n=== 2. Intentando Registrar Duplicado ===");
        hospital.registrarPaciente("1919", "Lamine Duplicado"); // Debería fallar

        System.out.println("\n=== 3. Busqueda Rapida O(1) ===");
        String docABuscar = "1616";
        Paciente encontrado = hospital.buscarPorDocumento(docABuscar);
        System.out.println("Buscando " + docABuscar + " -> Encontrado: " + encontrado);

        // Mostrar que el orden se mantiene intacto
        hospital.mostrarOrdenDeLlegada();
    }
}
