package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.ws.rs.core.Response;
import model.Token;
import security.ServletContextHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public enum TokenDao {
    instance;

    private HikariDataSource dataSource;
    TokenDao() {
        dataSource = CPDataSource.createNewDataSource();
    }

    // Deletes token from database. Returns true if logOut was completed, false otherwise.
    public boolean deleteToken(String token) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "DELETE FROM token WHERE token = ?";
        PreparedStatement tokenPS = connection.prepareStatement(sql);
        tokenPS.setString(1, token);
        int rowsAffected = tokenPS.executeUpdate();

        // Close connection.
        connection.close();

        if (rowsAffected > 0) {
            System.out.println("User with token " + token + " was logged out successfully.");
            return true;
        } else {
            System.out.println("User with token " + token + " attempted logout unsuccessfully. Token might not exist.");
            return false;
        }


    }

    public boolean addToken(Token token) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "INSERT INTO token(account_id, token, expiration, created_at, updated_at) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, token.getAccountId());
        ps.setString(2, token.getToken());
        ps.setTimestamp(3, token.getExpiration());
        ps.setTimestamp(4, token.getCreateDate());
        ps.setTimestamp(5, token.getUpdatedDate());
        int rowsAffected = ps.executeUpdate();

        // Close connection.
        connection.close();

        if (rowsAffected > 0) {
            return true;
        } else {
            return false;
        }
    }

    // Returns true if any rows were affected
    public boolean removeTokensByAccountId(int accountId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "DELETE FROM token WHERE token.account_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, accountId);
        int rowsAffected = ps.executeUpdate();

        // Close connection.
        connection.close();

        if (rowsAffected > 0) {
            return true;
        } else {
            return false;
        }
    }


}