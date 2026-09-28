package models;

import java.time.LocalDateTime;
import java.util.Map;

public class Pedido {
    private String id;
    private LocalDateTime fecha;
    private Cliente cliente;
    private Usuario trabajadorAsignado;
    private String estado;
    private Map<Producto, Integer> productos;
    private double total;

    public Pedido(String id, Cliente cliente, Usuario trabajadorAsignado, Map<Producto, Integer> productos, double total) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID de pedido inválido.");
        this.id = id;
        this.fecha = LocalDateTime.now();
        this.cliente = cliente;
        this.trabajadorAsignado = trabajadorAsignado;
        this.estado = "PENDIENTE";
        this.productos = productos;
        this.total = total;
    }

    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public Usuario getTrabajadorAsignado() { return trabajadorAsignado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Map<Producto, Integer> getProductos() { return productos; }
    public double getTotal() { return total; }
    
    public void setTrabajadorAsignado(Usuario trabajador) {
        this.trabajadorAsignado = trabajador;
    }
}
