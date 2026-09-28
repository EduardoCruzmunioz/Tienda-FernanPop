package persistence;

import models.Producto;
import java.util.Map;

public interface IPersistenciaProductos {
    void guardar(Map<String, Producto> productos);
    Map<String, Producto> cargar();
}
