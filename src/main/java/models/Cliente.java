package models;

import exceptions.UsuarioDuplicadoException;
import utils.Utils;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Cliente extends Usuario {
    private String direccionEnvio;
    private Map<Producto, Integer> carrito;
    private List<Pedido> historialPedidos;

    public Cliente(String id, String nombre, String email, String password, String direccionEnvio) {
        super(id, nombre, email, password);
        setDireccionEnvio(direccionEnvio);
        this.carrito = new HashMap<>();
        this.historialPedidos = new ArrayList<>();
    }
    
    public static Cliente registrar(String nombre, String email, String password, String direccion, Map<String, Usuario> mapaUsuarios) throws UsuarioDuplicadoException {
        Usuario.validarEmailDuplicado(email, mapaUsuarios);
        String nuevoId = Utils.generarId(mapaUsuarios.keySet(), "C");
        Cliente nuevo = new Cliente(nuevoId, nombre, email, password, direccion);
        mapaUsuarios.put(nuevo.getId(), nuevo);
        return nuevo;
    }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { 
        if (direccionEnvio == null || direccionEnvio.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de envío no puede estar vacía.");
        }
        this.direccionEnvio = direccionEnvio; 
    }

    public Map<Producto, Integer> getCarrito() { return carrito; }
    public List<Pedido> getHistorialPedidos() { return historialPedidos; }
}
