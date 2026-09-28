package models;

import exceptions.UsuarioDuplicadoException;
import utils.Utils;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Trabajador extends Usuario {
    private List<Pedido> pedidosAsignados;

    public Trabajador(String id, String nombre, String email, String password) {
        super(id, nombre, email, password);
        this.pedidosAsignados = new ArrayList<>();
    }

    public static Trabajador registrar(String nombre, String email, String password, Map<String, Usuario> mapaUsuarios) throws UsuarioDuplicadoException {
        Usuario.validarEmailDuplicado(email, mapaUsuarios);
        String nuevoId = Utils.generarId(mapaUsuarios.keySet(), "T");
        Trabajador nuevo = new Trabajador(nuevoId, nombre, email, password);
        mapaUsuarios.put(nuevo.getId(), nuevo);
        return nuevo;
    }

    public List<Pedido> getPedidosAsignados() { return pedidosAsignados; }
}
