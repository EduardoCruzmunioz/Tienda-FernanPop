package models;

public class ProductoDigital extends Producto {
    private double tamanoMb;
    private String licencia;

    public ProductoDigital(String id, String nombre, double precio, int stock, double tamanoMb, String licencia) {
        super(id, nombre, precio, stock);
        setTamanoMb(tamanoMb);
        setLicencia(licencia);
    }

    public double getTamanoMb() { return tamanoMb; }
    public void setTamanoMb(double tamanoMb) { 
        if (tamanoMb <= 0) throw new IllegalArgumentException("El tamaño en MB debe ser mayor a cero.");
        this.tamanoMb = tamanoMb; 
    }
    
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { 
        if (licencia == null || licencia.trim().isEmpty()) throw new IllegalArgumentException("La licencia no puede estar vacía.");
        this.licencia = licencia; 
    }
}
