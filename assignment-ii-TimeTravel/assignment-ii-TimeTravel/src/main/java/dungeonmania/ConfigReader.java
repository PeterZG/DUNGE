package dungeonmania;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigReader {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream input = new FileInputStream("src/main/java/dungeonmania/config.properties");
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getString(String key) {
        return CACHE.computeIfAbsent(key, k -> properties.getProperty(k));
    }

    public static <T> T getValue(String key, Class<T> clazz) {
        String value = getString(key);
        if (value == null) {
            throw new RuntimeException("Missing property: " + key);
        }
        if (clazz == String.class) {
            return clazz.cast(value);
        } else if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(value));
        } else if (clazz == Double.class) {
            return clazz.cast(Double.parseDouble(value));
        } else if (clazz == Boolean.class) {
            return clazz.cast(Boolean.parseBoolean(value));
        } else {
            throw new IllegalArgumentException("Unsupported property type: " + clazz.getName());
        }
    }
}
