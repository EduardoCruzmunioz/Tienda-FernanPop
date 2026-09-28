package controller;

import exceptions.*;
import models.*;
import persistence.*;

import java.util.*;

public class TiendaController {
    private Map<String, Producto> inventario;
    private Map<String, Usuario> usuarios;
    private List<Pedido> historialPedidos;
    
    private Usuario usuarioLogueado;

    public TiendaController() {
        cargarDatos();
    }

    private void cargarDatos() {
        // Asegurar que existe el directorio data/
        String folder = utils.Config.getInstance().getProperty("folder.data");
        new java.io.File(folder).mkdirs();
        
        IPersistenciaUsuarios gestorUsuarios = new persistence.GestorUsuarios();
        IPersistenciaProductos gestorProductos = new persistence.GestorProductos();
        IPersistenciaPedidos gestorPedidos = new persistence.GestorPedidos();
        
        this.usuarios = gestorUsuarios.cargar();
        this.inventario = gestorProductos.cargar();
        this.historialPedidos = gestorPedidos.cargar(this.usuarios, this.inventario);

        // Vincular los pedidos cargados a los objetos Cliente y Trabajador
        for (Pedido p : this.historialPedidos) {
            Cliente c = p.getCliente();
            if (c != null) {
                c.getHistorialPedidos().add(p);
            }
            Usuario u = p.getTrabajadorAsignado();
            if (u instanceof Trabajador t) {
                t.getPedidosAsignados().add(p);
            }
        }

        // Si es la primera vez (no hay usuarios ni inventario), sembramos la base de datos
        if (this.usuarios.isEmpty() && this.inventario.isEmpty()) {
            try {
                // 1 Admin
                Administrador.registrar("Admin Jefe", "admin@tienda.com", "1234", this.usuarios);
                // 1 Trabajador
                Trabajador.registrar("Paco Logística", "paco@tienda.com", "1234", this.usuarios);
                // 2 Clientes
                Cliente.registrar("Juan Pérez", "juan@mail.com", "1234", "Calle Falsa 123", this.usuarios);
                Cliente.registrar("María López", "maria@mail.com", "1234", "Avenida Siempreviva 742", this.usuarios);
                
                // 6 Productos (3 Físicos, 3 Digitales)
                String pf1 = altaProductoFisico("Teclado Mecánico", 50.0, 10, 1.2, 5.0);
                altaProductoFisico("Ratón Gaming", 30.0, 20, 0.5, 3.0);
                altaProductoFisico("Monitor 24 Pulgadas", 150.0, 5, 4.0, 10.0);
                String pd1 = altaProductoDigital("Licencia Windows 11", 25.0, 99, 15.0, "PERMANENTE");
                altaProductoDigital("Antivirus 1 Año", 15.0, 50, 5.0, "ANUAL");
                altaProductoDigital("Curso Java Avanzado", 10.0, 100, 20.0, "PERMANENTE");
                
                // 1 Pedido de prueba
                Cliente clienteTest = (Cliente) this.usuarios.values().stream().filter(u -> u.getEmail().equals("juan@mail.com")).findFirst().get();
                Trabajador trabTest = (Trabajador) this.usuarios.values().stream().filter(u -> u instanceof Trabajador).findFirst().get();
                java.util.Map<Producto, Integer> carroTest = new java.util.HashMap<>();
                carroTest.put(this.inventario.get(pf1), 2);
                carroTest.put(this.inventario.get(pd1), 1);
                Pedido pedTest = new Pedido("PED00001", clienteTest, trabTest, carroTest, 125.0);
                this.historialPedidos.add(pedTest);
                clienteTest.getHistorialPedidos().add(pedTest);
                trabTest.getPedidosAsignados().add(pedTest);

                // Forzar guardado inmediato para crear los archivos físicamente
                guardarDatos();
            } catch (Exception e) {
                System.out.println("Error generando datos iniciales: " + e.getMessage());
            }
        }
    }

    public void guardarDatos() {
        IPersistenciaUsuarios gestorUsuarios = new persistence.GestorUsuarios();
        IPersistenciaProductos gestorProductos = new persistence.GestorProductos();
        IPersistenciaPedidos gestorPedidos = new persistence.GestorPedidos();
        
        gestorUsuarios.guardar(this.usuarios);
        gestorProductos.guardar(this.inventario);
        gestorPedidos.guardar(this.historialPedidos);
    }

    public void registrarCliente(String nombre, String email, String password, String direccion) throws Exception {
        Cliente.registrar(nombre, email, password, direccion, this.usuarios);
    }

    public void registrarTrabajador(String nombre, String email, String password) throws Exception {
        Trabajador.registrar(nombre, email, password, this.usuarios);
    }

    public void registrarAdministrador(String nombre, String email, String password) throws Exception {
        Administrador.registrar(nombre, email, password, this.usuarios);
    }

    public void login(String email, String password) throws CredencialesInvalidasException {
        this.usuarioLogueado = Usuario.login(email, password, this.usuarios);
    }

    public void logout() {
        this.usuarioLogueado = null;
    }

    // --- GESTIÓN DE PRODUCTOS (CRUD) ---

    public String altaProductoFisico(String nombre, double precio, int stock, double pesoKg, double costeEnvio) {
        String nuevoId = utils.Utils.generarId(inventario.keySet(), "PF");
        ProductoFisico p = new ProductoFisico(nuevoId, nombre, precio, stock, pesoKg, costeEnvio);
        inventario.put(p.getId(), p);
        return nuevoId;
    }

    public String altaProductoDigital(String nombre, double precio, int stock, double tamanoMb, String licencia) {
        String nuevoId = utils.Utils.generarId(inventario.keySet(), "PD");
        ProductoDigital p = new ProductoDigital(nuevoId, nombre, precio, stock, tamanoMb, licencia);
        inventario.put(p.getId(), p);
        return nuevoId;
    }

    public void bajaProducto(String id) throws ProductoNoEncontradoException {
        Producto p = inventario.get(id);
        if (p == null) {
            throw new ProductoNoEncontradoException("No existe el producto con ID: " + id);
        }
        p.setActivo(false); // Soft Delete
    }

    public void bajaTrabajador(String id) throws Exception {
        Usuario u = usuarios.get(id);
        if (u == null || !(u instanceof Trabajador)) {
            throw new Exception("No existe un trabajador activo con ID: " + id);
        }
        u.setActivo(false); // Soft Delete
    }

    public void modificarStock(String id, int nuevoStock) throws ProductoNoEncontradoException {
        Producto p = inventario.get(id);
        if (p == null) throw new ProductoNoEncontradoException("No existe el producto con ID: " + id);
        p.setStock(nuevoStock); // Lanza IllegalArgumentException si es < 0 por nuestro blindaje
    }

    public void modificarPrecio(String id, double nuevoPrecio) throws ProductoNoEncontradoException {
        Producto p = inventario.get(id);
        if (p == null) throw new ProductoNoEncontradoException("No existe el producto con ID: " + id);
        p.setPrecio(nuevoPrecio); // Lanza IllegalArgumentException si es < 0 por nuestro blindaje
    }

    // --- GESTIÓN DEL CARRITO ---

    public void anadirAlCarrito(String idProducto, int cantidad) throws Exception {
        if (!(usuarioLogueado instanceof models.Cliente)) {
            throw new Exception("Solo los clientes pueden comprar.");
        }
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        
        Producto p = inventario.get(idProducto);
        if (p == null || !p.isActivo()) {
            throw new ProductoNoEncontradoException("Producto no disponible.");
        }
        
        Cliente c = (Cliente) usuarioLogueado;
        int cantidadActual = c.getCarrito().getOrDefault(p, 0);
        if (p.getStock() < (cantidadActual + cantidad)) {
            throw new Exception("No hay suficiente stock. Stock disponible: " + p.getStock());
        }
        
        c.getCarrito().put(p, cantidadActual + cantidad);
    }

    public void eliminarDelCarrito(String idProducto, int cantidad) throws Exception {
        Cliente c = (Cliente) usuarioLogueado;
        Producto p = inventario.get(idProducto);
        if (p == null || !c.getCarrito().containsKey(p)) {
            throw new Exception("El producto no está en el carrito.");
        }
        
        int nuevaCantidad = c.getCarrito().get(p) - cantidad;
        if (nuevaCantidad <= 0) {
            c.getCarrito().remove(p);
        } else {
            c.getCarrito().put(p, nuevaCantidad);
        }
    }

    public double calcularTotalCarrito() {
        if (!(usuarioLogueado instanceof Cliente)) return 0;
        Cliente c = (Cliente) usuarioLogueado;
        double total = 0;
        for (Map.Entry<Producto, Integer> entry : c.getCarrito().entrySet()) {
            total += entry.getKey().getPrecio() * entry.getValue();
        }
        return total;
    }

    public void vaciarCarrito() {
        if (usuarioLogueado instanceof Cliente c) {
            c.getCarrito().clear();
        }
    }

    public String tramitarPedido() throws Exception {
        if (!(usuarioLogueado instanceof Cliente)) throw new Exception("Solo clientes pueden comprar.");
        Cliente c = (Cliente) usuarioLogueado;
        
        if (c.getCarrito().isEmpty()) {
            throw new Exception("No puedes tramitar un pedido con el carrito vacío.");
        }
        
        // 1. Re-verificar stock antes de cobrar
        for (Map.Entry<Producto, Integer> entry : c.getCarrito().entrySet()) {
            Producto p = entry.getKey();
            if (p.getStock() < entry.getValue() || !p.isActivo()) {
                throw new Exception("El producto " + p.getNombre() + " ya no tiene stock o no está disponible.");
            }
        }
        
        // 2. Descontar stock definitivo
        for (Map.Entry<Producto, Integer> entry : c.getCarrito().entrySet()) {
            Producto p = entry.getKey();
            p.setStock(p.getStock() - entry.getValue());
        }
        
        // 3. Asignar Trabajador aleatorio (requisito del PDF)
        java.util.List<models.Trabajador> trabajadores = new java.util.ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof models.Trabajador && u.isActivo()) {
                trabajadores.add((models.Trabajador) u);
            }
        }
        models.Trabajador asignado = null;
        if (!trabajadores.isEmpty()) {
            int randomIndex = new java.util.Random().nextInt(trabajadores.size());
            asignado = trabajadores.get(randomIndex);
        }
        
        // 4. Generar ID y crear Pedido (clonando el carrito)
        java.util.Set<String> idsPedidos = new java.util.HashSet<>();
        for (models.Pedido p : historialPedidos) idsPedidos.add(p.getId());
        String nuevoId = utils.Utils.generarId(idsPedidos, "PED");
        
        double total = calcularTotalCarrito();
        Map<Producto, Integer> copiaCarrito = new HashMap<>(c.getCarrito());
        models.Pedido nuevoPedido = new models.Pedido(nuevoId, c, asignado, copiaCarrito, total);
        
        historialPedidos.add(nuevoPedido);
        c.getHistorialPedidos().add(nuevoPedido);
        if (asignado != null) {
            asignado.getPedidosAsignados().add(nuevoPedido);
        }
        
        c.getCarrito().clear();
        
        return nuevoId;
    }

    // --- GESTIÓN DE TRABAJADOR Y PEDIDOS ---

    public java.util.List<models.Pedido> getPedidosPorTrabajador(String idTrabajador) {
        Usuario u = usuarios.get(idTrabajador);
        if (u instanceof Trabajador t) return t.getPedidosAsignados();
        return new java.util.ArrayList<>();
    }

    public void actualizarEstadoPedido(String idPedido, String nuevoEstado) throws Exception {
        if (!(usuarioLogueado instanceof models.Trabajador) && !(usuarioLogueado instanceof models.Administrador)) {
            throw new Exception("Solo personal autorizado puede cambiar el estado de un pedido.");
        }
        
        models.Pedido encontrado = null;
        for (models.Pedido p : historialPedidos) {
            if (p.getId().equalsIgnoreCase(idPedido)) {
                encontrado = p;
                break;
            }
        }
        
        if (encontrado == null) throw new Exception("Pedido no encontrado.");
        
        // Si no es el Admin, validamos que el pedido sea suyo
        if (usuarioLogueado instanceof models.Trabajador) {
            if (encontrado.getTrabajadorAsignado() == null || !encontrado.getTrabajadorAsignado().getId().equals(usuarioLogueado.getId())) {
                throw new Exception("Este pedido no te pertenece o no tiene trabajador.");
            }
        }
        
        if (encontrado.getEstado().equalsIgnoreCase("ENVIADO")) {
            throw new Exception("El pedido ya está ENVIADO y su estado es inmodificable.");
        }
        
        encontrado.setEstado(nuevoEstado);
    }

    // --- CONTADORES PARA LA UI ---
    
    public int getCantidadProductosEnCarrito() {
        if (!(usuarioLogueado instanceof Cliente)) return 0;
        Cliente c = (Cliente) usuarioLogueado;
        return c.getCarrito().values().stream().mapToInt(Integer::intValue).sum();
    }

    public double getTotalIngresos() {
        return historialPedidos.stream()
            .filter(p -> p.getEstado().equalsIgnoreCase("ENVIADO"))
            .mapToDouble(models.Pedido::getTotal)
            .sum();
    }

    public String getProductoMasVendido() {
        return historialPedidos.stream()
            .flatMap(p -> p.getProductos().entrySet().stream())
            .collect(java.util.stream.Collectors.groupingBy(
                Map.Entry::getKey, 
                java.util.stream.Collectors.summingInt(Map.Entry::getValue)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey().getNombre() + " (" + e.getValue() + " ventas)")
            .orElse("Ninguno");
    }

    public double getTicketMedio() {
        return historialPedidos.stream()
            .mapToDouble(models.Pedido::getTotal)
            .average()
            .orElse(0.0);
    }

    public String getClienteConMasPedidos() {
        return historialPedidos.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                models.Pedido::getCliente, 
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey().getNombre() + " (" + e.getValue() + " pedidos)")
            .orElse("Ninguno");
    }

    public String exportarHistorialExcel() throws java.io.IOException {
        String ruta = utils.Config.getInstance().getProperty("folder.data") + "/reporte_excel.csv";
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(ruta))) {
            bw.write("ID_PEDIDO;FECHA;CLIENTE_EMAIL;TRABAJADOR;ESTADO;TOTAL_EUR\n");
            for (models.Pedido p : historialPedidos) {
                String asig = p.getTrabajadorAsignado() != null ? p.getTrabajadorAsignado().getEmail() : "SIN_ASIGNAR";
                bw.write(String.format("%s;%s;%s;%s;%s;%.2f\n", 
                    p.getId(), p.getFecha().toString(), p.getCliente().getEmail(), asig, p.getEstado(), p.getTotal()));
            }
        }
        return ruta;
    }

    public void reasignarPedido(String idPedido, String idNuevoTrabajador) throws Exception {
        if (!(usuarioLogueado instanceof models.Administrador)) {
            throw new Exception("Solo el Administrador puede reasignar cargas de trabajo.");
        }
        
        models.Pedido encontrado = null;
        for (models.Pedido p : historialPedidos) {
            if (p.getId().equalsIgnoreCase(idPedido)) {
                encontrado = p;
                break;
            }
        }
        
        if (encontrado == null) throw new Exception("Pedido no encontrado.");
        if (encontrado.getEstado().equalsIgnoreCase("ENVIADO")) {
            throw new Exception("No puedes reasignar un pedido que ya ha sido ENVIADO.");
        }
        
        Usuario nuevoTrabajador = usuarios.get(idNuevoTrabajador);
        if (nuevoTrabajador == null || !nuevoTrabajador.isActivo()) {
            throw new Exception("El ID proporcionado no pertenece a un usuario activo.");
        }
        if (!(nuevoTrabajador instanceof models.Trabajador) && !(nuevoTrabajador instanceof models.Administrador)) {
            throw new Exception("Solo se puede asignar a un Trabajador o a un Administrador.");
        }
        
        encontrado.setTrabajadorAsignado(nuevoTrabajador);
    }

    public long getCantidadPedidosPendientesCliente(String idCliente) {
        Usuario u = usuarios.get(idCliente);
        if (u instanceof Cliente c) {
            return c.getHistorialPedidos().stream().filter(p -> !p.getEstado().equalsIgnoreCase("ENVIADO")).count();
        }
        return 0;
    }

    public java.util.List<models.Pedido> getPedidosPorCliente(String idCliente) {
        Usuario u = usuarios.get(idCliente);
        if (u instanceof Cliente c) return c.getHistorialPedidos();
        return new java.util.ArrayList<>();
    }

    // --- GETTERS ---
    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public Map<Producto, Integer> getCarritoActual() { 
        if (usuarioLogueado instanceof Cliente c) return c.getCarrito();
        return new HashMap<>(); 
    }
    public Map<String, Producto> getInventario() { return inventario; }
    public Map<String, Usuario> getUsuarios() { return usuarios; }
    public java.util.List<models.Pedido> getHistorialPedidos() { return historialPedidos; }
}
