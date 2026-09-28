package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Config instancia;
    private Properties properties;

    private Config() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("No se pudo cargar config.properties. Usando valores por defecto.");
            properties.setProperty("folder.data", "data");
            properties.setProperty("file.usuarios", "data/usuarios.csv");
            properties.setProperty("file.productos", "data/productos.csv");
            properties.setProperty("file.pedidos", "data/pedidos.csv");
        }
    }

    public static Config getInstance() {
        if (instancia == null) {
            instancia = new Config();
        }
        return instancia;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Usado exclusivamente para los Tests Unitarios (inyectar rutas temporales)
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
