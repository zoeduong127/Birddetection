package dao;

import com.zaxxer.hikari.HikariDataSource;
import model.Account;
import model.LoginCredentials;
import model.Token;
import security.TokenManager;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Calendar;

import static resources.AccountResource.addTime;


public enum AccountDao {
    instance;
    private HikariDataSource dataSource;

    AccountDao() {
        dataSource = CPDataSource.createNewDataSource();
    }

    public boolean addAccount(Account account) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "INSERT INTO account(username,email,password,tel,salt) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, account.getUsername());
        ps.setString(2, account.getEmail());
        ps.setString(3, account.getPasswordHash());
        ps.setString(4, account.getTelephone());
        ps.setString(5, account.getSalt());
        int rowsAffected = ps.executeUpdate();

        // Close connection
        connection.close();

        if (rowsAffected > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteAccountById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "DELETE FROM account a where a.account_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, id);

        int rowsAffected = ps.executeUpdate();

        // Close connection
        connection.close();

        if (rowsAffected > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the account object that corresponds to the given account id.
     * @param accountId
     * @return The account if exists. Returns null otherwise.
     */
    public Account getAccountById(int accountId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM account a WHERE a.account_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, accountId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            try {
                Account result = new Account(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6));
                // Close connection
                connection.close();
                return result;
            } catch (IllegalArgumentException e) {
                // Close connection
                connection.close();
                return null;
            }
        } else {
            // Close connection
            connection.close();
            return null;
        }
    }

    public boolean loginCheck(String email, String passwordHashed) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM account a where a.email = ? AND a.password = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, email);
        ps.setString(2, passwordHashed);

        ResultSet rs = ps.executeQuery();


        if (rs.next()) {
            // Close connection
            connection.close();
            return true;
        } else {
            // Close connection
            connection.close();
            return false;
        }
    }

    public Account getAccountByEmail(String email) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM account a WHERE a.email = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();


        if (rs.next()) {
            try {
                Account result = new Account(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6));
                // Close connection
                connection.close();
                return result;
            } catch (IllegalArgumentException e) {
                System.err.println("Illegal argument exception occurred: " + e);
                // Close connection
                connection.close();
                return null;
            }
        } else {
            // Close connection
            connection.close();
            return null;
        }
    }

    /**
     * Given an account object containing a username and email, it checks if an account with the same username or email already exists.
     * @param account The account to test
     * @return True of the account already exists, false otherwise.
     */
    public boolean accountExists(Account account) throws SQLException {
        String sql = "SELECT * FROM account WHERE account.username = ? OR account.email = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, account.getUsername());
        ps.setString(2, account.getEmail());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            // Close connection
            connection.close();
            return true;
        } else {
            // Close connection
            connection.close();
            return false;
        }
    }

    public Token getAuthTokenByCredentials(LoginCredentials credentials) throws SQLException {

        Account account = getAccountByEmail(credentials.getEmail());

        Timestamp expiration = addTime(new Timestamp(System.currentTimeMillis()), 24, Calendar.HOUR);
        String token = TokenManager.generateToken(account.getUsername(), account.getId(), expiration);

        Token tokenToAdd = new Token();
        tokenToAdd.setAccountId(account.getId());
        tokenToAdd.setToken(token);
        tokenToAdd.setExpiration(expiration);
        tokenToAdd.setCreateDate(new Timestamp(System.currentTimeMillis()));
        tokenToAdd.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

        // Removes existing tokens associated with this account
        TokenDao.instance.removeTokensByAccountId(account.getId());

        if (!TokenDao.instance.addToken(tokenToAdd)) {
            return null;
        } else {
            return tokenToAdd;
        }
    }

    public boolean changePasswordById(int accountId, String newPassword) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Account account = AccountDao.instance.getAccountById(accountId);

            if (account == null) {
                return false;
            }

            String sql = "UPDATE account SET password = ? WHERE account_id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            System.out.println(newPassword + account.getSalt());
            ps.setString(1, hash256(newPassword + account.getSalt()));
            ps.setInt(2, accountId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public String hash256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
