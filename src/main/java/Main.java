import controller.TiendaController;
import exceptions.CredencialesInvalidasException;
import exceptions.UsuarioDuplicadoException;
import models.Administrador;
import models.Usuario;
import persistence.GestorPedidos;
import persistence.GestorProductos;
import persistence.GestorUsuarios;
import views.MenuPrincipal;
import utils.Utils;

import java.util.Scanner;

public class Main {
    void main() {
        TiendaController controller = new TiendaController();
        Scanner scanner = new Scanner(System.in);
        
        boolean salir = false;
        while (!salir) {
            MenuPrincipal.mostrarOpciones();
            String opcion = scanner.nextLine();
            
            switch (opcion) {
                case "1" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarCabeceraLogin();
                    MenuPrincipal.pedirDato("Email");
                    String email = scanner.nextLine();
                    
                    MenuPrincipal.pedirDato("Contraseña");
                    String pass = scanner.nextLine();
                    
                    try {
                        Utils.limpiaPantalla();
                        controller.login(email, pass);
                        Usuario u = controller.getUsuarioLogueado();
                        MenuPrincipal.mostrarMensaje("¡Login correcto! Bienvenido, %s (%s).", u.getNombre(), u.getClass().getSimpleName());
                        
                        if (u instanceof Administrador) {
                            flujoAdmin(controller, scanner);
                        } else if (u instanceof models.Cliente) {
                            flujoCliente(controller, scanner);
                        } else if (u instanceof models.Trabajador) {
                            flujoTrabajador(controller, scanner);
                        } else {
                            MenuPrincipal.mostrarMensaje("Menú en construcción para tu rol.");
                        }
                        
                        controller.logout();
                        
                    } catch (CredencialesInvalidasException e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "2" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarCabeceraRegistro();
                    MenuPrincipal.pedirDato("Nombre");
                    String nombre = scanner.nextLine();
                    
                    MenuPrincipal.pedirDato("Email");
                    String email = scanner.nextLine();
                    
                    MenuPrincipal.pedirDato("Contraseña");
                    String pass = scanner.nextLine();
                    
                    MenuPrincipal.pedirDato("Dirección de envío");
                    String direccion = scanner.nextLine();
                    
                    try {
                        Utils.limpiaPantalla();
                        controller.registrarCliente(nombre, email, pass, direccion);
                        MenuPrincipal.mostrarMensaje("¡Registro completado con éxito! Ya puedes iniciar sesión.");
                    } catch (UsuarioDuplicadoException | IllegalArgumentException e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error al registrar: %s", e.getMessage());
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error inesperado: %s", e.getMessage());
                    }
                }
                case "3" -> {
                    Utils.limpiaPantalla();
                    salir = true;
                    MenuPrincipal.mostrarMensaje("Guardando datos...");
                    controller.guardarDatos();
                    MenuPrincipal.mostrarMensaje("¡Hasta pronto!");
                }
                default -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("Opción incorrecta.");
                }
            }
        }
        scanner.close();
    }

    private static void flujoAdmin(TiendaController controller, Scanner scanner) {
        boolean atras = false;
        while (!atras) {
            int totalPeds = controller.getHistorialPedidos().size();
            double ingresos = controller.getTotalIngresos();
            MenuPrincipal.mostrarOpcionesAdmin(totalPeds, ingresos);
            String opt = scanner.nextLine();
            
            switch (opt) {
                case "1" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("Nombre");
                        String n = scanner.nextLine();
                        MenuPrincipal.pedirDato("Precio");
                        double p = Double.parseDouble(scanner.nextLine());
                        MenuPrincipal.pedirDato("Stock");
                        int s = Integer.parseInt(scanner.nextLine());
                        MenuPrincipal.pedirDato("Peso (Kg)");
                        double peso = Double.parseDouble(scanner.nextLine());
                        MenuPrincipal.pedirDato("Coste Envío");
                        double coste = Double.parseDouble(scanner.nextLine());
                        
                        String id = controller.altaProductoFisico(n, p, s, peso, coste);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Producto Físico creado con ID: %s", id);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "2" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("Nombre");
                        String n = scanner.nextLine();
                        MenuPrincipal.pedirDato("Precio");
                        double p = Double.parseDouble(scanner.nextLine());
                        MenuPrincipal.pedirDato("Stock");
                        int s = Integer.parseInt(scanner.nextLine());
                        MenuPrincipal.pedirDato("Tamaño (MB)");
                        double t = Double.parseDouble(scanner.nextLine());
                        MenuPrincipal.pedirDato("Licencia");
                        String l = scanner.nextLine();
                        
                        String id = controller.altaProductoDigital(n, p, s, t, l);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Producto Digital creado con ID: %s", id);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "3" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("ID del producto a borrar");
                        String id = scanner.nextLine();
                        controller.bajaProducto(id);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Producto %s eliminado correctamente.", id);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "4" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("Nombre");
                        String n = scanner.nextLine();
                        MenuPrincipal.pedirDato("Email");
                        String em = scanner.nextLine();
                        MenuPrincipal.pedirDato("Contraseña");
                        String p = scanner.nextLine();
                        
                        controller.registrarTrabajador(n, em, p);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Trabajador registrado correctamente.");
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "5" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("ID del Trabajador a dar de baja");
                        String id = scanner.nextLine();
                        controller.bajaTrabajador(id);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Trabajador %s desactivado (Baja lógica).", id);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "6" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("--- HISTORIAL GENERAL DE PEDIDOS ---");
                    var todos = controller.getHistorialPedidos();
                    if (todos.isEmpty()) {
                        MenuPrincipal.mostrarMensaje("Todavía no hay pedidos en el sistema.");
                    } else {
                        for (models.Pedido p : todos) {
                            MenuPrincipal.mostrarTarjetaPedido(p);
                        }
                    }
                    System.out.println();
                }
                case "7" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("ID del pedido a reasignar");
                        String idPed = scanner.nextLine();
                        MenuPrincipal.pedirDato("ID del NUEVO trabajador");
                        String idTrab = scanner.nextLine();
                        
                        controller.reasignarPedido(idPed, idTrab);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("¡Pedido %s reasignado correctamente al trabajador %s!", idPed, idTrab);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error al reasignar: %s", e.getMessage());
                    }
                }
                case "8" -> {
                    Utils.limpiaPantalla();
                    try {
                        MenuPrincipal.pedirDato("ID del pedido");
                        String id = scanner.nextLine();
                        
                        MenuPrincipal.mostrarMensaje("Estados: 1. PENDIENTE | 2. EN RECOGIDA | 3. EMPAQUETANDO | 4. LISTO PARA ENVIAR | 5. ENVIADO | 6. INCIDENCIA");
                        MenuPrincipal.pedirDato("Selecciona nuevo estado (1-6)");
                        String estadoOpt = scanner.nextLine();
                        
                        String nuevoEstado = switch (estadoOpt) {
                            case "1" -> "PENDIENTE";
                            case "2" -> "EN RECOGIDA";
                            case "3" -> "EMPAQUETANDO";
                            case "4" -> "LISTO PARA ENVIAR";
                            case "5" -> "ENVIADO";
                            case "6" -> "INCIDENCIA";
                            default -> throw new IllegalArgumentException("Estado no válido.");
                        };
                        
                        controller.actualizarEstadoPedido(id, nuevoEstado);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("¡El estado del pedido %s forzado a %s por el Admin!", id, nuevoEstado);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "9" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("--- ESTADÍSTICAS AVANZADAS ---");
                    MenuPrincipal.mostrarMensaje("Producto más vendido: %s", controller.getProductoMasVendido());
                    MenuPrincipal.mostrarMensaje("Ticket Medio (Gasto por pedido): %.2f€", controller.getTicketMedio());
                    MenuPrincipal.mostrarMensaje("Usuario con más pedidos: %s", controller.getClienteConMasPedidos());
                    System.out.println("\n(Pulsa ENTER para continuar)");
                    scanner.nextLine();
                    Utils.limpiaPantalla();
                }
                case "10" -> {
                    Utils.limpiaPantalla();
                    try {
                        String ruta = controller.exportarHistorialExcel();
                        MenuPrincipal.mostrarMensaje("¡Historial exportado con éxito para Excel!");
                        MenuPrincipal.mostrarMensaje("Archivo guardado en: %s", ruta);
                    } catch (Exception e) {
                        MenuPrincipal.mostrarMensaje("Error exportando a Excel: %s", e.getMessage());
                    }
                    System.out.println("\n(Pulsa ENTER para continuar)");
                    scanner.nextLine();
                    Utils.limpiaPantalla();
                }
                case "11" -> {
                    Utils.limpiaPantalla();
                    atras = true;
                }
                default -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("Opción incorrecta.");
                }
            }
        }
    }

    private static void flujoCliente(TiendaController controller, Scanner scanner) {
        boolean atras = false;
        while (!atras) {
            int enCarrito = controller.getCantidadProductosEnCarrito();
            long pedPendientes = controller.getCantidadPedidosPendientesCliente(controller.getUsuarioLogueado().getId());
            
            MenuPrincipal.mostrarOpcionesCliente(enCarrito, pedPendientes);
            String opt = scanner.nextLine();
            
            switch (opt) {
                case "1" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("--- CATÁLOGO DE PRODUCTOS ---");
                    for (models.Producto p : controller.getInventario().values()) {
                        if (p.isActivo() && p.getStock() > 0) {
                            MenuPrincipal.mostrarTarjetaProducto(p);
                        }
                    }
                    System.out.println();
                }
                case "2" -> {
                    try {
                        MenuPrincipal.pedirDato("ID del producto");
                        String id = scanner.nextLine();
                        MenuPrincipal.pedirDato("Cantidad");
                        int c = Integer.parseInt(scanner.nextLine());
                        controller.anadirAlCarrito(id, c);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("¡Añadido al carrito correctamente!");
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "3" -> {
                    Utils.limpiaPantalla();
                    var carrito = controller.getCarritoActual();
                    if (carrito.isEmpty()) {
                        MenuPrincipal.mostrarMensaje("El carrito está vacío.");
                    } else {
                        MenuPrincipal.mostrarMensaje("--- TU CARRITO ---");
                        for (var entry : carrito.entrySet()) {
                            models.Producto p = entry.getKey();
                            int cant = entry.getValue();
                            MenuPrincipal.mostrarLineaCarrito(p.getId(), p.getNombre(), cant, p.getPrecio() * cant);
                        }
                        MenuPrincipal.mostrarTotalCarrito(controller.calcularTotalCarrito());
                    }
                }
                case "4" -> {
                    try {
                        MenuPrincipal.pedirDato("ID del producto a restar/quitar");
                        String id = scanner.nextLine();
                        MenuPrincipal.pedirDato("Cantidad a restar");
                        int c = Integer.parseInt(scanner.nextLine());
                        controller.eliminarDelCarrito(id, c);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Carrito actualizado.");
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "5" -> {
                    Utils.limpiaPantalla();
                    controller.vaciarCarrito();
                    MenuPrincipal.mostrarMensaje("El carrito ha sido vaciado.");
                }
                case "6" -> {
                    Utils.limpiaPantalla();
                    try {
                        String idPedido = controller.tramitarPedido();
                        MenuPrincipal.mostrarMensaje("¡Compra realizada con éxito! Tu número de pedido es: %s", idPedido);
                    } catch (Exception e) {
                        MenuPrincipal.mostrarMensaje("Error al tramitar pedido: %s", e.getMessage());
                    }
                }
                case "7" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("--- MIS PEDIDOS REALIZADOS ---");
                    var misPed = controller.getPedidosPorCliente(controller.getUsuarioLogueado().getId());
                    if (misPed.isEmpty()) {
                        MenuPrincipal.mostrarMensaje("Aún no has realizado ninguna compra.");
                    } else {
                        for (models.Pedido p : misPed) {
                            MenuPrincipal.mostrarTarjetaPedido(p);
                        }
                    }
                    System.out.println();
                }
                case "8" -> {
                    Utils.limpiaPantalla();
                    atras = true;
                }
                default -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("Opción incorrecta.");
                }
            }
        }
    }

    private static void flujoTrabajador(TiendaController controller, Scanner scanner) {
        boolean atras = false;
        while (!atras) {
            // Contamos solo los pedidos que no han sido enviados
            int pedAsignados = (int) controller.getPedidosPorTrabajador(controller.getUsuarioLogueado().getId())
                                        .stream().filter(p -> !p.getEstado().equalsIgnoreCase("ENVIADO")).count();
            
            MenuPrincipal.mostrarOpcionesTrabajador(pedAsignados);
            String opt = scanner.nextLine();
            
            switch (opt) {
                case "1" -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("--- MIS PEDIDOS ASIGNADOS ---");
                    var misPedidos = controller.getPedidosPorTrabajador(controller.getUsuarioLogueado().getId());
                    if (misPedidos.isEmpty()) {
                        MenuPrincipal.mostrarMensaje("No tienes pedidos asignados actualmente.");
                    } else {
                        for (models.Pedido p : misPedidos) {
                            MenuPrincipal.mostrarTarjetaPedido(p);
                        }
                    }
                    System.out.println();
                }
                case "2" -> {
                    try {
                        MenuPrincipal.pedirDato("ID del pedido");
                        String id = scanner.nextLine();
                        
                        MenuPrincipal.mostrarMensaje("Estados: 1. PENDIENTE | 2. EN RECOGIDA | 3. EMPAQUETANDO | 4. LISTO PARA ENVIAR | 5. ENVIADO | 6. INCIDENCIA");
                        MenuPrincipal.pedirDato("Selecciona nuevo estado (1-6)");
                        String estadoOpt = scanner.nextLine();
                        
                        String nuevoEstado = switch (estadoOpt) {
                            case "1" -> "PENDIENTE";
                            case "2" -> "EN RECOGIDA";
                            case "3" -> "EMPAQUETANDO";
                            case "4" -> "LISTO PARA ENVIAR";
                            case "5" -> "ENVIADO";
                            case "6" -> "INCIDENCIA";
                            default -> throw new IllegalArgumentException("Estado no válido.");
                        };
                        
                        controller.actualizarEstadoPedido(id, nuevoEstado);
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("¡El estado del pedido %s se ha actualizado a %s!", id, nuevoEstado);
                    } catch (Exception e) {
                        Utils.limpiaPantalla();
                        MenuPrincipal.mostrarMensaje("Error: %s", e.getMessage());
                    }
                }
                case "3" -> {
                    Utils.limpiaPantalla();
                    atras = true;
                }
                default -> {
                    Utils.limpiaPantalla();
                    MenuPrincipal.mostrarMensaje("Opción incorrecta.");
                }
            }
        }
    }
}
