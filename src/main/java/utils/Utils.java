package utils;

import java.util.Set;

public class Utils {
    
    public static void limpiaPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // Metodo universal para generar IDs con prefijo y 5 dígitos (ej. C00001)
    public static String generarId(Set<String> keysExistentes, String prefijo) {
        int max = 0;
        for (String k : keysExistentes) {
            if (k != null && k.startsWith(prefijo)) {
                try {
                    int num = Integer.parseInt(k.substring(prefijo.length()));
                    if (num > max) max = num;
                } catch (Exception e) {
                    // Ignorar si hay algún ID corrupto
                }
            }
        }
        return String.format("%s%05d", prefijo, max + 1);
    }
}
