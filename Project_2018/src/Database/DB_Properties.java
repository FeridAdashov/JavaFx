package Database;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DB_Properties {
    Properties properties;

    public DB_Properties() {
        FileReader reader;
        try {
            reader = new FileReader(Files.walk(Paths.get(""))
                    .filter(Files::isRegularFile)
                    .filter(e -> e.getFileName().toString().equals("db.properties")).findAny().get().toFile());
            properties = new Properties();
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDriver() {
        return properties.getProperty("db.driver");
    }

    public String getURL() {
        return properties.getProperty("db.URL");
    }

    public String getUsername() {
        return properties.getProperty("db.username");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }

}