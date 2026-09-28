package views;

public class MenuPrincipal {

    public static void mostrarOpciones() {
        System.out.print("""
                
                ╭──────────────────────────────────────────────────╮
                │                 TIENDA FERNANPOP                 │
                ├──────────────────────────────────────────────────┤
                │          Bienvenido al sistema de gestión        │
                ├──────────────────────────────────────────────────┤
                │ 1. Iniciar sesión                                │
                │ 2. Registrarse como Cliente                      │
                │ 3. Guardar y Salir                               │
                ╰──────────────────────────────────────────────────╯
                Elige una opción: """);
    }

    public static void mostrarCabeceraLogin() {
        System.out.print("""
                
                ╭──────────────────────────────────────────────────╮
                │                      LOGIN                       │
                ╰──────────────────────────────────────────────────╯
                """);
    }

    public static void mostrarOpcionesAdmin(int totalPeds, double ingresos) {
        System.out.println("\n╭──────────────────────────────────────────────────╮");
        System.out.println("│                  PANEL DE ADMIN                  │");
        System.out.println("├──────────────────────────────────────────────────┤");
        String info = String.format("Total Pedidos: %d | Ingresos: %.2f€", totalPeds, ingresos);
        System.out.printf("│ 📊 %-45s │\n", info);
        System.out.print("""
                ├──────────────────────────────────────────────────┤
                │ 1. Alta Producto Físico                          │
                │ 2. Alta Producto Digital                         │
                │ 3. Baja de Producto                              │
                │ 4. Alta Trabajador                               │
                │ 5. Baja Trabajador                               │
                │ 6. Ver todos los pedidos                         │
                │ 7. Reasignar carga (Pedido)                      │
                │ 8. Modificar estado pedido                       │
                │ 9. Ver estadísticas avanzadas                    │
                │ 10. Exportar Historial a Excel (CSV)             │
                │ 11. Cerrar sesión                                │
                ╰──────────────────────────────────────────────────╯
                Elige una opción: """);
    }

    public static void mostrarOpcionesCliente(int enCarrito, long pedPendientes) {
        System.out.println("\n╭──────────────────────────────────────────────────╮");
        System.out.println("│                  ZONA DE COMPRA                  │");
        System.out.println("├──────────────────────────────────────────────────┤");
        String info = String.format("Carrito: %d | Pendientes: %d", enCarrito, pedPendientes);
        System.out.printf("│ 🛒 %-45s │\n", info);
        System.out.print("""
                ├──────────────────────────────────────────────────┤
                │ 1. Ver catálogo de productos                     │
                │ 2. Añadir al carrito                             │
                │ 3. Ver mi carrito                                │
                │ 4. Quitar del carrito                            │
                │ 5. Vaciar carrito                                │
                │ 6. Tramitar Pedido                               │
                │ 7. Mis pedidos (Historial)                       │
                │ 8. Cerrar sesión                                 │
                ╰──────────────────────────────────────────────────╯
                Elige una opción: """);
    }

    public static void mostrarTarjetaProducto(models.Producto p) {
        System.out.println("╭──────────────────────────────────────────────────╮");
        
        if (p instanceof models.ProductoFisico pf) {
            System.out.printf("│ 📦 ID: %-41s │\n", pf.getId());
            System.out.printf("│ 🏷️ PROD: %-39s │\n", pf.getNombre());
            String ps = String.format("Precio: %.2f€  |  Stock: %d unds.", pf.getPrecio(), pf.getStock());
            System.out.printf("│ 💰 %-45s │\n", ps);
            String ex = String.format("Peso: %.2f Kg  |  Envío: %.2f€", pf.getPesoKg(), pf.getCosteEnvio());
            System.out.printf("│ ⚖️ %-45s │\n", ex);
        } else if (p instanceof models.ProductoDigital pd) {
            System.out.printf("│ 💻 ID: %-41s │\n", pd.getId());
            System.out.printf("│ 🏷️ PROD: %-39s │\n", pd.getNombre());
            String ps = String.format("Precio: %.2f€  |  Stock: %d unds.", pd.getPrecio(), pd.getStock());
            System.out.printf("│ 💰 %-45s │\n", ps);
            String ex = String.format("Tamaño: %.2f MB |  Licencia: %s", pd.getTamanoMb(), pd.getLicencia());
            System.out.printf("│ 💾 %-45s │\n", ex);
        }
        
        System.out.println("╰──────────────────────────────────────────────────╯");
    }

    public static void mostrarOpcionesTrabajador(int asignadosPendientes) {
        System.out.println("\n╭──────────────────────────────────────────────────╮");
        System.out.println("│               PANEL DE TRABAJADOR                │");
        System.out.println("├──────────────────────────────────────────────────┤");
        String info = String.format("Pedidos en curso: %d", asignadosPendientes);
        System.out.printf("│ 📥 %-45s │\n", info);
        System.out.print("""
                ├──────────────────────────────────────────────────┤
                │ 1. Ver mis pedidos asignados                     │
                │ 2. Actualizar estado pedido                      │
                │ 3. Cerrar sesión                                 │
                ╰──────────────────────────────────────────────────╯
                Elige una opción: """);
    }

    public static void mostrarTarjetaPedido(models.Pedido p) {
        System.out.println("╭──────────────────────────────────────────────────╮");
        System.out.printf("│ 📦 PEDIDO: %-37s │\n", p.getId());
        System.out.printf("│ 👤 Cliente: %-36s │\n", p.getCliente().getNombre());
        String asig = p.getTrabajadorAsignado() != null ? p.getTrabajadorAsignado().getId() : "SIN ASIGNAR";
        System.out.printf("│ 👷 Trabajador: %-33s │\n", asig);
        System.out.printf("│ 📌 Estado: %-37s │\n", p.getEstado());
        System.out.printf("│ 💰 Total: %-37.2f€ │\n", p.getTotal());
        System.out.println("╰──────────────────────────────────────────────────╯");
    }

    public static void mostrarCabeceraRegistro() {
        System.out.print("""
                
                ╭──────────────────────────────────────────────────╮
                │                     REGISTRO                     │
                ╰──────────────────────────────────────────────────╯
                """);
    }

    public static void pedirDato(String campo) {
        System.out.printf("%s: ", campo);
    }

    public static void mostrarMensaje(String formato, Object... args) {
        System.out.printf(formato + "\n", args);
    }
    
    public static void mostrarLineaCarrito(String id, String nombre, int cantidad, double subtotal) {
        System.out.printf("│ 🛒 [%s] %s x%d  -->  %.2f€\n", id, nombre, cantidad, subtotal);
    }

    public static void mostrarTotalCarrito(double total) {
        System.out.printf("\n│ 💰 TOTAL A PAGAR: %.2f€\n\n", total);
    }
}
