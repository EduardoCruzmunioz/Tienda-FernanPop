package persistence;

import models.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorPedidos implements IPersistenciaPedidos {
    private static final String ARCHIVO = utils.Config.getInstance().getProperty("file.pedidos");

    public void guardar(List<Pedido> pedidos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Pedido p : pedidos) {
                StringBuilder sb = new StringBuilder();
                sb.append(p.getId()).append(";")
                  .append(p.getFecha().toString()).append(";")
                  .append(p.getCliente().getId()).append(";")
                  .append(p.getTrabajadorAsignado() != null ? p.getTrabajadorAsignado().getId() : "").append(";")
                  .append(p.getEstado()).append(";")
                  .append(p.getTotal()).append(";");
                
                List<String> lineasStr = new ArrayList<>();
                for (Map.Entry<Producto, Integer> entry : p.getProductos().entrySet()) {
                    lineasStr.add(entry.getKey().getId() + "," + entry.getValue());
                }
                sb.append(String.join("|", lineasStr));
                
                bw.write(sb.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error guardando pedidos: " + e.getMessage());
        }
    }

    public List<Pedido> cargar(Map<String, Usuario> usuariosBase, Map<String, Producto> inventarioBase) {
        List<Pedido> pedidos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(";");
                String id = d[0];
                
                Cliente cliente = (Cliente) usuariosBase.get(d[2]);
                Trabajador trabajador = null;
                if (!d[3].isEmpty()) trabajador = (Trabajador) usuariosBase.get(d[3]);
                
                String estado = d[4];
                double total = Double.parseDouble(d[5]);
                
                Map<Producto, Integer> productosPedido = new HashMap<>();
                if (d.length > 6 && !d[6].isEmpty()) {
                    String[] lineasArray = d[6].split("\\|");
                    for (String l : lineasArray) {
                        String[] lData = l.split(",");
                        Producto p = inventarioBase.get(lData[0]);
                        if(p != null) productosPedido.put(p, Integer.parseInt(lData[1]));
                    }
                }
                
                Pedido p = new Pedido(id, cliente, trabajador, productosPedido, total);
                p.setEstado(estado);
                pedidos.add(p);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Error leyendo pedidos: " + e.getMessage());
        }
        return pedidos;
    }
}
