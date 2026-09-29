import java.util.*;

public class PlataformaVentas {

    // Clase interna que representa un Producto en la plataforma de ventas masivas
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

        @Override
        public String toString() {
            return String.format("[Cod: %s] %s | Precio: $%.2f | Cat: %s", codigo, nombre, precio, categoria);
        }
    }

    // Estructura 1: O(1) para buscar rápidamente un producto por su código único
    private final Map<String, Producto> mapaPorCodigo = new HashMap<>();

    // Estructura 2: O(1) para insertar miles de productos nuevos por hora al inicio de la lista (Novedades)
    private final LinkedList<Producto> listaNovedades = new LinkedList<>();

    // Estructura 3: O(1) de acceso para agrupar y filtrar rápidamente productos por categoría
    private final Map<String, List<Producto>> mapaPorCategoria = new HashMap<>();

    // Estructura 4: Mantiene los productos ordenados por precio de manera nativa (O(log n) al insertar)
    // Se utiliza un Comparator que desempata con el código si los precios son idénticos
    private final TreeSet<Producto> setPorPrecio = new TreeSet<>((p1, p2) -> {
        int comp = Double.compare(p1.precio, p2.precio);
        if (comp == 0) {
            return p1.codigo.compareTo(p2.codigo);
        }
        return comp;
    });

    // Operación: Insertar miles de productos nuevos por hora al inicio de la lista e indexar
    public void insertarProductoNuevo(String codigo, String nombre, double precio, String categoria) {
        if (mapaPorCodigo.containsKey(codigo)) {
            System.out.println("[ERROR] El codigo " + codigo + " ya existe en la plataforma.");
            return;
        }

        Producto nuevoProducto = new Producto(codigo, nombre, precio, categoria);

        // 1. Indexar en el mapa principal de búsquedas O(1)
        mapaPorCodigo.put(codigo, nuevoProducto);

        // 2. Insertar estrictamente al inicio de la lista en O(1)
        listaNovedades.addFirst(nuevoProducto);

        // 3. Registrar en su respectiva categoría para filtrado inmediato O(1)
        mapaPorCategoria.computeIfAbsent(categoria, k -> new ArrayList<>()).add(nuevoProducto);

        // 4. Insertar en el árbol ordenado por precio O(log n)
        setPorPrecio.add(nuevoProducto);

        System.out.println("[OK] Producto insertado e indexado: " + nombre);
    }

    // Operación: Buscar productos por código (Muy frecuente) -> Tiempo: O(1)
    public Producto buscarPorCodigo(String codigo) {
        return mapaPorCodigo.get(codigo);
    }

    // Operación: Mostrar productos ordenados por precio -> Tiempo: O(n) al iterar el árbol ya ordenado
    public void mostrarProductosOrdenadosPorPrecio() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("PRODUCTOS ORDENADOS POR PRECIO (Menor a Mayor)");
        System.out.println("--------------------------------------------------");
        for (Producto p : setPorPrecio) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------------------");
    }

    // Operación: Filtrar por categoría -> Tiempo: O(1) para obtener la lista del mapa
    public void mostrarProductosPorCategoria(String categoria) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("FILTRANDO PRODUCTOS POR CATEGORIA: " + categoria.toUpperCase());
        System.out.println("--------------------------------------------------");
        List<Producto> productosCat = mapaPorCategoria.get(categoria);
        if (productosCat == null || productosCat.isEmpty()) {
            System.out.println("No hay productos registrados en esta categoria.");
        } else {
            for (Producto p : productosCat) {
                System.out.println(p);
            }
        }
        System.out.println("--------------------------------------------------");
    }

    // Operación secundaria: Mostrar el orden cronológico inverso (Los últimos agregados primero)
    public void mostrarListaNovedades() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("ULTIMOS PRODUCTOS INGRESADOS (Historial de Novedades)");
        System.out.println("--------------------------------------------------");
        for (Producto p : listaNovedades) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------------------");
    }

    // Simulación de la plataforma de ventas con datos de prueba
    public static void main(String[] args) {
        PlataformaVentas plataforma = new PlataformaVentas();

        System.out.println("=== REGISTRANDO PRODUCTOS MASIVOS ===");
        // Se simula la inserción consecutiva en lotes por hora
        plataforma.insertarProductoNuevo("PROD-40", "Monitor Gamer 144Hz", 299.99, "Electronica");
        plataforma.insertarProductoNuevo("PROD-10", "Teclado Mecanico RGB", 89.50, "Electronica");
        plataforma.insertarProductoNuevo("PROD-20", "Silla Ergonomica", 180.00, "Oficina");
        plataforma.insertarProductoNuevo("PROD-30", "Escritorio Elevable L", 450.00, "Oficina");

        System.out.println("\n=== PRUEBA: BUSQUEDA RAPIDA POR CODIGO O(1) ===");
        String codigoBuscar = "PROD-10";
        Producto buscado = plataforma.buscarPorCodigo(codigoBuscar);
        System.out.println("Resultado de busqueda (" + codigoBuscar + "): " + (buscado != null ? buscado : "No encontrado"));

        // Demostración del filtrado lógico estructurado por categoría
        plataforma.mostrarProductosPorCategoria("Oficina");

        // Demostración del ordenamiento por costo total de producto
        plataforma.mostrarProductosOrdenadosPorPrecio();

        // Demostración de que la LinkedList mantuvo a los últimos elementos agregados estrictamente al inicio
        plataforma.mostrarListaNovedades();
    }
}
