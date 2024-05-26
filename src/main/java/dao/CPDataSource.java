package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import security.ServletContextHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CPDataSource {
    private static HikariConfig config = new HikariConfig();

    private CPDataSource(){}

    static {

        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Properties prop = new Properties();

        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+ "\\src\\main\\webapp\\WEB-INF\\config\\dao.config")) {
            prop.load(fis);
        } catch (IOException ignored) {
            String filename = ServletContextHolder.getServletContext().getRealPath("/WEB-INF/config/dao.config");
            try (FileInputStream fis = new FileInputStream(filename)) {
                prop.load(fis);
            } catch (IOException ex) {
                System.out.println("IOException: Failed to open dao.config file containing database credentials.\n" + ex.getMessage());
            }
        }

        config.setJdbcUrl(prop.getProperty("database.jdbc-url"));
        config.setUsername(prop.getProperty("database.username"));
        config.setPassword(prop.getProperty("database.password"));
    }

    public static HikariDataSource createNewDataSource() {
        return new HikariDataSource(config);
    }
}
