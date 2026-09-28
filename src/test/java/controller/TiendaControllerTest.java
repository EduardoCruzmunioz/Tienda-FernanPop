package controller;

import exceptions.ProductoNoEncontradoException;
import models.Producto;
import models.ProductoDigital;
import models.ProductoFisico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TiendaControllerTest {
    
    private TiendaController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new TiendaController();
        // Limpiamos los mapas en memoria para tests aislados
        controller.getInventario().clear();
        controller.getUsuarios().clear();
        
        // Registramos y logueamos un cliente por defecto para poder probar el carrito
        controller.registrarCliente("Paco", "paco@mail.com", "1234", "Calle 1");
        controller.login("paco@mail.com", "1234");
    }

    // --- TESTS PRODUCTOS ---
    @Test
    void testAltaProductoFisico_CreaYGuardaCorrectamente() {
        String id = controller.altaProductoFisico("Teclado", 50.0, 10, 1.2, 5.0);
        assertTrue(id.startsWith("PF"));
        assertTrue(controller.getInventario().containsKey(id));
    }

    @Test
    void testBajaProducto_SoftDeleteCorrectamente() throws ProductoNoEncontradoException {
        String id = controller.altaProductoFisico("Ratón", 20.0, 5, 0.5, 3.0);
        controller.bajaProducto(id);
        assertFalse(controller.getInventario().get(id).isActivo());
    }

    @Test
    void testModificarStock_NegativoLanzaExcepcionPorBlindaje() {
        String id = controller.altaProductoFisico("Monitor", 200.0, 10, 5.0, 10.0);
        assertThrows(IllegalArgumentException.class, () -> {
            controller.modificarStock(id, -5);
        });
    }

    // --- TESTS CARRITO ---
    @Test
    void testAnadirAlCarrito_Exito() throws Exception {
        String id = controller.altaProductoFisico("Teclado", 50.0, 10, 1.2, 5.0);
        controller.anadirAlCarrito(id, 2);
        
        Producto p = controller.getInventario().get(id);
        assertEquals(2, controller.getCarritoActual().get(p));
    }

    @Test
    void testAnadirAlCarrito_FallaPorFaltaDeStock() {
        String id = controller.altaProductoFisico("Ratón", 20.0, 2, 0.5, 3.0);
        
        Exception e = assertThrows(Exception.class, () -> {
            controller.anadirAlCarrito(id, 3);
        });
        assertTrue(e.getMessage().contains("No hay suficiente stock"));
    }

    @Test
    void testEliminarDelCarrito_ExitoRestando() throws Exception {
        String id = controller.altaProductoFisico("Cable", 5.0, 20, 0.1, 1.0);
        controller.anadirAlCarrito(id, 5);
        controller.eliminarDelCarrito(id, 2);
        
        Producto p = controller.getInventario().get(id);
        assertEquals(3, controller.getCarritoActual().get(p));
    }

    @Test
    void testEliminarDelCarrito_EliminaCompletamente() throws Exception {
        String id = controller.altaProductoFisico("Cable", 5.0, 20, 0.1, 1.0);
        controller.anadirAlCarrito(id, 5);
        controller.eliminarDelCarrito(id, 5); // Quitamos todos
        
        Producto p = controller.getInventario().get(id);
        assertFalse(controller.getCarritoActual().containsKey(p));
    }

    @Test
    void testCalcularTotalCarrito() throws Exception {
        String id1 = controller.altaProductoFisico("Prod1", 10.0, 10, 1.0, 0.0);
        String id2 = controller.altaProductoDigital("Prod2", 20.0, 10, 1.0, "KEY");
        
        controller.anadirAlCarrito(id1, 2); // 10 * 2 = 20
        controller.anadirAlCarrito(id2, 1); // 20 * 1 = 20
        
        assertEquals(40.0, controller.calcularTotalCarrito());
    }

    @Test
    void testTramitarPedido_RestaStockYGeneraPedido() throws Exception {
        // Necesitamos un trabajador activo para que se le asigne
        controller.registrarTrabajador("Currito", "curro@mail.com", "1234");
        
        String id = controller.altaProductoFisico("Teclado", 50.0, 10, 1.2, 5.0);
        controller.anadirAlCarrito(id, 2);
        
        String idPedido = controller.tramitarPedido();
        
        assertTrue(idPedido.startsWith("PED"));
        assertTrue(controller.getCarritoActual().isEmpty()); // El carrito se vacía tras pagar
        assertEquals(8, controller.getInventario().get(id).getStock()); // El stock se ha restado de forma definitiva
    }

    // --- TESTS LOGÍSTICA (ESTADOS, REASIGNACIÓN E INGRESOS) ---
    @Test
    void testActualizarEstadoPedidoYContadores() throws Exception {
        controller.registrarTrabajador("Trab", "trab@mail.com", "1234");
        String idProd = controller.altaProductoFisico("Monitor", 100.0, 5, 2.0, 5.0);
        controller.anadirAlCarrito(idProd, 1);
        String idPedido = controller.tramitarPedido();

        models.Pedido ped = controller.getHistorialPedidos().get(0);
        
        // El cliente logueado (Paco) debe tener 1 pendiente
        assertEquals(1, controller.getCantidadPedidosPendientesCliente(controller.getUsuarioLogueado().getId()));
        
        // Loguearse como el trabajador asignado
        controller.login("trab@mail.com", "1234");
        
        // El trabajador avanza el estado a ENVIADO
        controller.actualizarEstadoPedido(idPedido, "ENVIADO");
        assertEquals("ENVIADO", ped.getEstado());
        
        // Volvemos al cliente y comprobamos que ya no está pendiente sino procesado
        controller.login("paco@mail.com", "1234");
        assertEquals(0, controller.getCantidadPedidosPendientesCliente(controller.getUsuarioLogueado().getId()));
        assertEquals(1, controller.getPedidosPorCliente(controller.getUsuarioLogueado().getId()).size());
    }

    @Test
    void testReasignarPedidoYTotalIngresos_SoloAdmin() throws Exception {
        controller.registrarTrabajador("T1", "t1@mail.com", "1234");
        controller.registrarTrabajador("T2", "t2@mail.com", "1234");
        
        String idProd = controller.altaProductoFisico("Raton", 10.0, 5, 0.5, 2.0);
        controller.anadirAlCarrito(idProd, 1);
        String idPedido = controller.tramitarPedido();
        
        // Creamos y logueamos un Admin
        controller.getUsuarios().put("ADMIN", new models.Administrador("ADMIN", "Jefazo", "admin@mail.com", "admin123"));
        controller.login("admin@mail.com", "admin123");
        
        // Reasignar al Admin a sí mismo
        controller.reasignarPedido(idPedido, "ADMIN");
        assertEquals("ADMIN", controller.getHistorialPedidos().get(0).getTrabajadorAsignado().getId());
        
        // El admin cambia el estado a ENVIADO forzosamente
        controller.actualizarEstadoPedido(idPedido, "ENVIADO");
        
        assertEquals(10.0, controller.getTotalIngresos());
    }
}
