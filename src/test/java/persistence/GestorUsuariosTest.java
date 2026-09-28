package persistence;

import models.Administrador;
import models.Cliente;
import models.Trabajador;
import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GestorUsuariosTest {
    private GestorUsuarios gestor;
    private final String TEST_FILE = "data/test_usuarios.csv";

    @BeforeEach
    void setUp() {
        new File("data").mkdirs();
        Config.getInstance().setProperty("file.usuarios", TEST_FILE);
        gestor = new GestorUsuarios();
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGuardarYCargarUsuarios() throws Exception {
        Map<String, Usuario> mapaOriginal = new HashMap<>();
        
        Administrador admin = new Administrador("ADMIN1", "Jefe", "jefe@mail.com", "1234");
        Trabajador trab = new Trabajador("T001", "Paco", "paco@mail.com", "1234");
        Cliente cli = new Cliente("C001", "Juan", "juan@mail.com", "1234", "Calle 1");
        cli.setActivo(false); // Probar que guarda el estado
        
        mapaOriginal.put(admin.getId(), admin);
        mapaOriginal.put(trab.getId(), trab);
        mapaOriginal.put(cli.getId(), cli);
        
        gestor.guardar(mapaOriginal);
        
        Map<String, Usuario> mapaCargado = gestor.cargar();
        
        assertEquals(3, mapaCargado.size());
        assertTrue(mapaCargado.get("ADMIN1") instanceof Administrador);
        assertTrue(mapaCargado.get("T001") instanceof Trabajador);
        
        Cliente cliCargado = (Cliente) mapaCargado.get("C001");
        assertNotNull(cliCargado);
        assertEquals("Calle 1", cliCargado.getDireccionEnvio());
        assertFalse(cliCargado.isActivo());
    }
}
