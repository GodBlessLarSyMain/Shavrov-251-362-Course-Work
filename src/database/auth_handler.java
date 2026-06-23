package database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class auth_handler extends db_handler {

    public int login(String login, String password) throws SQLException {
        String hashedLogin = hashString(login);
        String hashedPassword = hashString(password);

        String sql = """
                SELECT id_role
                FROM tvcompany.`user`
                WHERE login = ? AND password = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedLogin);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_role");
                }
            }
        }

        return -1;
    }

    public boolean userExists(String login) throws SQLException {
        String hashedLogin = hashString(login);

        String sql = """
                SELECT COUNT(*) AS count_user
                FROM tvcompany.`user`
                WHERE login = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedLogin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count_user") > 0;
                }
            }
        }

        return false;
    }

    public void register(String login, String password, int idRole) throws SQLException {
        String hashedLogin = hashString(login);
        String hashedPassword = hashString(password);

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            Integer idAgent = null;
            Integer idCustomer = null;

            if (idRole == 1) {
                idAgent = createAgent(login);
            } else if (idRole == 2) {
                idCustomer = createCustomer(login);
            }

            String sql = """
                    INSERT INTO tvcompany.`user`
                        (id_role, id_agent, id_customer, login, password)
                    VALUES
                        (?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idRole);

                if (idAgent == null) {
                    ps.setNull(2, Types.INTEGER);
                } else {
                    ps.setInt(2, idAgent);
                }

                if (idCustomer == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, idCustomer);
                }

                ps.setString(4, hashedLogin);
                ps.setString(5, hashedPassword);

                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private int createAgent(String login) throws SQLException {
        String sql = """
                INSERT INTO tvcompany.agents
                    (full_name, mail)
                VALUES
                    (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, login);
            ps.setString(2, login);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Не удалось создать рекламного агента");
    }

    private int createCustomer(String login) throws SQLException {
        String sql = """
                INSERT INTO tvcompany.customers
                    (number, details)
                VALUES
                    (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, login);
            ps.setString(2, "Новый заказчик");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Не удалось создать заказчика");
    }

    public Integer getAgentIdByLoginAndPassword(String login, String password) throws SQLException {
        String hashedLogin = hashString(login);
        String hashedPassword = hashString(password);

        String sql = """
                SELECT id_agent
                FROM tvcompany.`user`
                WHERE login = ? AND password = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedLogin);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject("id_agent");

                    if (value == null) {
                        return null;
                    }

                    return ((Number) value).intValue();
                }
            }
        }

        return null;
    }

    public Integer getCustomerIdByLoginAndPassword(String login, String password) throws SQLException {
        String hashedLogin = hashString(login);
        String hashedPassword = hashString(password);

        String sql = """
                SELECT id_customer
                FROM tvcompany.`user`
                WHERE login = ? AND password = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedLogin);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject("id_customer");

                    if (value == null) {
                        return null;
                    }

                    return ((Number) value).intValue();
                }
            }
        }

        return null;
    }

    public String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хэширования", e);
        }
    }
}
