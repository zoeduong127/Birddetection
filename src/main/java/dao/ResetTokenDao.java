package dao;

import com.zaxxer.hikari.HikariDataSource;
import model.ResetToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum ResetTokenDao {
    instance;
    private HikariDataSource dataSource;
    ResetTokenDao() {
        dataSource = CPDataSource.createNewDataSource();
    }

    public boolean addToken(ResetToken resetToken) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO reset_token (account_id, token, expiration) VALUES (?,?,?);";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, resetToken.getAccountId());
            ps.setString(2, resetToken.getToken());
            ps.setTimestamp(3, resetToken.getExpiration());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public boolean removeTokensByAccountId(int accountId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM reset_token WHERE account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, accountId);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public boolean resetPassword(String token, String newPassword) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM reset_token WHERE token = ?;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, token);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return false;
            }

            int accountId = rs.getInt(2);

            if  (!AccountDao.instance.changePasswordById(accountId, newPassword)) {
                return false;
            }

            ResetTokenDao.instance.removeTokensByAccountId(accountId);

            return true;
        }
    }
}
