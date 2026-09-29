import java.util.*;

public class PlataformaVentasBenchmark {

    static class Producto {
        String codigo;
        String nombre;
        double precio;
        String categoria;

        public Producto(String codigo, String nombre, double precio, String categoria) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }
    }

    private final Map<String, Producto> mapaPorCodigo = new HashMap<>();
    private final LinkedList<Producto> listaNovedades = new LinkedList<>();
    private final Map<String, List<Producto>> mapaPorCategoria = new HashMap<>();
    private final TreeSet<Producto> setPorPrecio = new TreeSet<>((p1, p2) -> {
        int comp = Double.compare(p1.precio, p2.precio);
        if (comp == 0) return p1.codigo.compareTo(p2.codigo);
        return comp;
    });

    public void insertarProducto(String codigo, String nombre, double precio, String categoria) {
        Producto nuevo = new Producto(codigo, nombre, precio, categoria);
        mapaPorCodigo.put(codigo, nuevo);
        listaNovedades.addFirst(nuevo);
        mapaPorCategoria.computeIfAbsent(categoria, k -> new ArrayList<>()).add(nuevo);
        setPorPrecio.add(nuevo);
    }

    public Producto buscarPorCodigo(String codigo) {
        return mapaPorCodigo.get(codigo);
    }

    public void limpiar() {
        mapaPorCodigo.clear();
        listaNovedades.clear();
        mapaPorCategoria.clear();
        setPorPrecio.clear();
    }

    public static void main(String[] args) {
        int[] tamanos = {10000, 100000};
        String[] categoriasSimuladas = {"Electronica", "Oficina", "Hogar", "Deportes", "Moda"};
        Random random = new Random(42); // Semilla fija para consistencia

        System.out.println("=============================================================================================");
        System.out.println("         BENCHMARK DE RENDIMIENTO (FASE 4): PLATAFORMA DE VENTAS MASIVAS                     ");
        System.out.println("=============================================================================================");
        System.out.printf("%-10s | %-22s | %-20s | %-15s | %-12s\n", 
                "PRODUCTOS", "T. INSERCION PARALELA", "T. BUSQUEDA CODIGO", "T. ITERAR ARBOL", "MEMORIA (MB)");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (int n : tamanos) {
            PlataformaVentasBenchmark plataforma = new PlataformaVentasBenchmark();
            
            // Forzar recolección de basura para aislar la medición de memoria
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

            // 1. Medir Tiempo de Inserción Masiva Simultánea (Inyectar al inicio, mapear, categorizar y arbolar)
            long inicioInsercion = System.nanoTime();
            for (int i = 0; i < n; i++) {
                String cod = "PROD-" + i;
                double precio = 10.0 + (random.nextDouble() * 1000.0);
                String cat = categoriasSimuladas[i % categoriasSimuladas.length];
                plataforma.insertarProducto(cod, "Item Masivo " + i, precio, cat);
            }
            long finInsercion = System.nanoTime();
            double tiempoInsercionMs = (finInsercion - inicioInsercion) / 1_000_000.0;

            // 2. Medir Tiempo de Búsqueda Frecuente (Peor caso: el último elemento del HashMap)
            String codigoABuscar = "PROD-" + (n - 1);
            long inicioBusqueda = System.nanoTime();
            plataforma.buscarPorCodigo(codigoABuscar);
            long finBusqueda = System.nanoTime();
            double tiempoBusquedaMs = (finBusqueda - inicioBusqueda) / 1_000_000.0;

            // 3. Medir Tiempo de lectura/ordenamiento nativo (Recorrer el TreeSet completo por precio)
            long inicioIterar = System.nanoTime();
            int contador = 0;
            for (Producto p : plataforma.setPorPrecio) {
                contador++; // Simula la lectura secuencial
            }
            long finIterar = System.nanoTime();
            double tiempoIterarMs = (finIterar - inicioIterar) / 1_000_000.0;

            // 4. Calcular el uso de memoria RAM aproximado
            long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();
            double memoriaUtilizadaMb = (memoriaDespues - memoriaAntes) / (1024.0 * 1024.0);
            if (memoriaUtilizadaMb < 0) memoriaUtilizadaMb = 1.0; 

            System.out.printf("%-10d | %-19.2f ms | %-17.5f ms | %-12.4f ms | %-12.2f\n", 
                    n, tiempoInsercionMs, tiempoBusquedaMs, tiempoIterarMs, memoriaUtilizadaMb);
            
            plataforma.limpiar();
        }
        System.out.println("=============================================================================================");
    }
}

