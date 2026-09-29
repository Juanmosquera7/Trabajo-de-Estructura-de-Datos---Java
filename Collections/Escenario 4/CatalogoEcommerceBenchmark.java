import java.util.*;

public class CatalogoEcommerceBenchmark {

    static class Producto {
        String codigo;
        String nombre;
        double precio;

        public Producto(String codigo, String nombre, double precio) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    // Estructura 1: Búsquedas inmediatas por código único
    private final Map<String, Producto> indexCodigo = new HashMap<>();

    // Estructura 2: Mantenimiento continuo del orden jerárquico de precios
    private final TreeSet<Producto> indexPrecio = new TreeSet<>((p1, p2) -> {
        int comp = Double.compare(p1.precio, p2.precio);
        if (comp == 0) return p1.codigo.compareTo(p2.codigo); // Desempate por código único
        return comp;
    });

    public void insertarProducto(String codigo, String nombre, double precio) {
        Producto nuevo = new Producto(codigo, nombre, precio);
        indexCodigo.put(codigo, nuevo);
        indexPrecio.add(nuevo);
    }

    public Producto buscarPorCodigo(String codigo) {
        return indexCodigo.get(codigo);
    }

    public void limpiar() {
        indexCodigo.clear();
        indexPrecio.clear();
    }

    public static void main(String[] args) {
        int[] tamanos = {100, 1000, 10000, 100000};
        Random random = new Random(101); // Semilla fija para reproducibilidad

        System.out.println("==========================================================================================");
        System.out.println("         BENCHMARK DE RENDIMIENTO (FASE 4): CATALOGO DE PRODUCTOS E-COMMERCE              ");
        System.out.println("==========================================================================================");
        System.out.printf("%-12s | %-20s | %-20s | %-12s\n", "PRODUCTOS", "T. INSERCION (ms)", "T. BUSQUEDA (ms)", "MEMORIA (MB)");
        System.out.println("------------------------------------------------------------------------------------------");

        for (int n : tamanos) {
            CatalogoEcommerceBenchmark catalogo = new CatalogoEcommerceBenchmark();
            
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

            // 1. Inserción Constante Masiva
            long inicioIns = System.nanoTime();
            for (int i = 0; i < n; i++) {
                double precioAleatorio = 5.0 + (random.nextDouble() * 2000.0);
                catalogo.insertarProducto("SKU-" + i, "Producto Base " + i, precioAleatorio);
            }
            long finIns = System.nanoTime();
            double tInsercion = (finIns - inicioIns) / 1_000_000.0;

            // 2. Búsqueda Frecuente (Peor caso en mapa)
            String skuBuscar = "SKU-" + (n - 1);
            long inicioBusq = System.nanoTime();
            catalogo.buscarPorCodigo(skuBuscar);
            long finBusq = System.nanoTime();
            double tBusqueda = (finBusq - inicioBusq) / 1_000_000.0;

            // 3. Medición del consumo aproximado de memoria RAM
            long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();
            double memoriaMb = (memoriaDespues - memoriaAntes) / (1024.0 * 1024.0);
            if (memoriaMb < 0) memoriaMb = 0.1;

            System.out.printf("%-12d | %-20.3f | %-20.5f | %-12.2f\n", n, tInsercion, tBusqueda, memoriaMb);
            
            catalogo.limpiar();
        }
        System.out.println("==========================================================================================");
    }
}
