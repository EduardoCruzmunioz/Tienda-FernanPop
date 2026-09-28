package models;

public class ProductoFisico extends Producto {
    private double pesoKg;
    private double costeEnvio;

    public ProductoFisico(String id, String nombre, double precio, int stock, double pesoKg, double costeEnvio) {
        super(id, nombre, precio, stock);
        setPesoKg(pesoKg);
        setCosteEnvio(costeEnvio);
    }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { 
        if (pesoKg <= 0) throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        this.pesoKg = pesoKg; 
    }
    
    public double getCosteEnvio() { return costeEnvio; }
    public void setCosteEnvio(double costeEnvio) { 
        if (costeEnvio < 0) throw new IllegalArgumentException("El coste de envío no puede ser negativo.");
        this.costeEnvio = costeEnvio; 
    }
}
