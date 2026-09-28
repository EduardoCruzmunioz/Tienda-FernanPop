package persistence;

import models.Usuario;
import models.Administrador;
import models.Trabajador;
import models.Cliente;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GestorUsuarios implements IPersistenciaUsuarios {
    private static final String ARCHIVO = utils.Config.getInstance().getProperty("file.usuarios");

    public void guardar(Map<String, Usuario> usuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Usuario u : usuarios.values()) {
                String base = String.format("%s;%s;%s;%s;%b", u.getId(), u.getNombre(), u.getEmail(), u.getPassword(), u.isActivo());
                if (u instanceof Administrador) {
                    bw.write("ADMIN;" + base + "\n");
                } else if (u instanceof Trabajador) {
                    bw.write("TRABAJADOR;" + base + "\n");
                } else if (u instanceof Cliente) {
                    Cliente c = (Cliente) u;
                    bw.write("CLIENTE;" + base + ";" + c.getDireccionEnvio() + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public Map<String, Usuario> cargar() {
        Map<String, Usuario> usuarios = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(";");
                String tipo = d[0];
                String id = d[1];
                String nombre = d[2];
                String email = d[3];
                String pass = d[4];
                boolean activo = Boolean.parseBoolean(d[5]);
                
                Usuario u = null;
                if (tipo.equals("ADMIN")) u = new Administrador(id, nombre, email, pass);
                else if (tipo.equals("TRABAJADOR")) u = new Trabajador(id, nombre, email, pass);
                else if (tipo.equals("CLIENTE")) u = new Cliente(id, nombre, email, pass, d[6]);

                if (u != null) {
                    u.setActivo(activo);
                    usuarios.put(id, u);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Error leyendo usuarios: " + e.getMessage());
        }
        return usuarios;
    }
}
