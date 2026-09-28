package persistence;

import models.Producto;
import models.ProductoDigital;
import models.ProductoFisico;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GestorProductosTest {
    private GestorProductos gestor;
    private final String TEST_FILE = "data/test_productos.csv";

    @BeforeEach
    void setUp() {
        new File("data").mkdirs();
        Config.getInstance().setProperty("file.productos", TEST_FILE);
        gestor = new GestorProductos();
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGuardarYCargarProductos() {
        Map<String, Producto> mapaOriginal = new HashMap<>();
        
        ProductoFisico pf = new ProductoFisico("PF001", "Teclado", 50.0, 10, 1.2, 5.0);
        ProductoDigital pd = new ProductoDigital("PD001", "Licencia", 20.0, 99, 15.0, "ANUAL");
        pd.setActivo(false);
        
        mapaOriginal.put(pf.getId(), pf);
        mapaOriginal.put(pd.getId(), pd);
        
        gestor.guardar(mapaOriginal);
        
        Map<String, Producto> mapaCargado = gestor.cargar();
        
        assertEquals(2, mapaCargado.size());
        
        ProductoFisico pfCargado = (ProductoFisico) mapaCargado.get("PF001");
        assertEquals("Teclado", pfCargado.getNombre());
        assertEquals(1.2, pfCargado.getPesoKg());
        assertTrue(pfCargado.isActivo());
        
        ProductoDigital pdCargado = (ProductoDigital) mapaCargado.get("PD001");
        assertEquals("ANUAL", pdCargado.getLicencia());
        assertFalse(pdCargado.isActivo());
    }
}
