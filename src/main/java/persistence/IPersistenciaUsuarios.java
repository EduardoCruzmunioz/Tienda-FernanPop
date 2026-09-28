package persistence;

import models.Usuario;
import java.util.Map;

public interface IPersistenciaUsuarios {
    void guardar(Map<String, Usuario> usuarios);
    Map<String, Usuario> cargar();
}
