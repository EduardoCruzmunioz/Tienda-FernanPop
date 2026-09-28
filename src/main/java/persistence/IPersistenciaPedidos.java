package persistence;

import models.Pedido;
import models.Producto;
import models.Usuario;
import java.util.List;
import java.util.Map;

public interface IPersistenciaPedidos {
    void guardar(List<Pedido> pedidos);
    List<Pedido> cargar(Map<String, Usuario> usuariosBase, Map<String, Producto> inventarioBase);
}
