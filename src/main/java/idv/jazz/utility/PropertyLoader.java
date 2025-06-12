package idv.jazz.utility;

import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    private final Properties properties = new Properties();

    public PropertyLoader(String fileNameInClasspath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileNameInClasspath)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new RuntimeException("無法找到 properties 檔案: " + fileNameInClasspath);
            }
        } catch (Exception e) {
            throw new RuntimeException("讀取 properties 發生錯誤: " + e.getMessage(), e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}

