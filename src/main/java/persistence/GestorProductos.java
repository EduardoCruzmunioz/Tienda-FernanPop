package persistence;

import models.Producto;
import models.ProductoFisico;
import models.ProductoDigital;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GestorProductos implements IPersistenciaProductos {
    private static final String ARCHIVO = utils.Config.getInstance().getProperty("file.productos");

    public void guardar(Map<String, Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Producto p : productos.values()) {
                if (p instanceof ProductoFisico) {
                    ProductoFisico pf = (ProductoFisico) p;
                    bw.write(String.format("FISICO;%s;%s;%s;%d;%s;%s;%b\n", 
                        pf.getId(), pf.getNombre(), pf.getPrecio(), pf.getStock(), pf.getPesoKg(), pf.getCosteEnvio(), pf.isActivo()));
                } else if (p instanceof ProductoDigital) {
                    ProductoDigital pd = (ProductoDigital) p;
                    bw.write(String.format("DIGITAL;%s;%s;%s;%d;%s;%s;%b\n", 
                        pd.getId(), pd.getNombre(), pd.getPrecio(), pd.getStock(), pd.getTamanoMb(), pd.getLicencia(), pd.isActivo()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando productos: " + e.getMessage());
        }
    }

    public Map<String, Producto> cargar() {
        Map<String, Producto> productos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(";");
                String id = d[1];
                Producto p = null;
                if (d[0].equals("FISICO")) {
                    p = new ProductoFisico(id, d[2], Double.parseDouble(d[3]), Integer.parseInt(d[4]), Double.parseDouble(d[5]), Double.parseDouble(d[6]));
                } else if (d[0].equals("DIGITAL")) {
                    p = new ProductoDigital(id, d[2], Double.parseDouble(d[3]), Integer.parseInt(d[4]), Double.parseDouble(d[5]), d[6]);
                }
                if (p != null) {
                    if (d.length > 7) p.setActivo(Boolean.parseBoolean(d[7]));
                    productos.put(id, p);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Error leyendo productos: " + e.getMessage());
        }
        return productos;
    }
}
