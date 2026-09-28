package models;

import exceptions.UsuarioDuplicadoException;
import java.util.Map;

public class Administrador extends Usuario {
    public Administrador(String id, String nombre, String email, String password) {
        super(id, nombre, email, password);
    }

    public static Administrador registrar(String nombre, String email, String password, Map<String, Usuario> mapaUsuarios) throws UsuarioDuplicadoException {
        Usuario.validarEmailDuplicado(email, mapaUsuarios);
        
        // Si solo hay un administrador en todo el sistema, le fijamos el ID a "ADMIN"
        if (mapaUsuarios.containsKey("ADMIN")) {
            throw new UsuarioDuplicadoException("Ya existe un administrador en el sistema.");
        }
        
        Administrador nuevo = new Administrador("ADMIN", nombre, email, password);
        mapaUsuarios.put(nuevo.getId(), nuevo);
        return nuevo;
    }
}
