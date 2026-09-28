package models;

import java.util.Objects;

public abstract class Producto {
    private String id;
    private String nombre;
    private double precio;
    private int stock;
    private boolean activo;

    public Producto(String id, String nombre, double precio, int stock) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("El ID no puede estar vacío.");
        this.id = id;
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
        this.activo = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() { return id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        this.nombre = nombre; 
    }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { 
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio; 
    }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { 
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        this.stock = stock; 
    }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
