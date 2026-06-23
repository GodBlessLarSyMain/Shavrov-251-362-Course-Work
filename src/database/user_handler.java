package database;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class user_handler extends db_handler {

    public void create_order(
            int idCustomer,
            String fullName,
            String agentMail,
            String number,
            String details,
            int idBroadcast,
            int idAdvertising,
            BigDecimal summ,
            BigDecimal agentPercentage,
            String date,
            String time
    ) throws SQLException {

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            int idAgent = getOrCreateAgent(fullName, agentMail);

            updateCustomer(idCustomer, number, details);

            int idAgreement = createAgreement(idCustomer, idAgent, summ, agentPercentage);

            createDateTime(
                    idBroadcast,
                    idAgreement,
                    idAdvertising,
                    date,
                    time
            );

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    public void create_order(
            String fullName,
            String agentMail,
            String number,
            String details,
            int idBroadcast,
            int idAdvertising,
            BigDecimal summ,
            BigDecimal agentPercentage,
            String date,
            String time
    ) throws SQLException {

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            int idAgent = getOrCreateAgent(fullName, agentMail);
            int idCustomer = createCustomer(number, details);
            int idAgreement = createAgreement(idCustomer, idAgent, summ, agentPercentage);

            createDateTime(
                    idBroadcast,
                    idAgreement,
                    idAdvertising,
                    date,
                    time
            );

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private int getOrCreateAgent(String fullName, String agentMail) throws SQLException {
        String selectAgent = """
                SELECT id_agent
                FROM tvcompany.agents
                WHERE mail = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(selectAgent)) {
            ps.setString(1, agentMail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idAgent = rs.getInt("id_agent");

                    updateAgentFullName(idAgent, fullName);

                    return idAgent;
                }
            }
        }

        String insertAgent = """
                INSERT INTO tvcompany.agents
                    (full_name, mail)
                VALUES
                    (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                insertAgent,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, fullName);
            ps.setString(2, agentMail);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Не удалось создать агента");
    }

    private void updateAgentFullName(int idAgent, String fullName) throws SQLException {
        String updateAgent = """
                UPDATE tvcompany.agents
                SET full_name = ?
                WHERE id_agent = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(updateAgent)) {
            ps.setString(1, fullName);
            ps.setInt(2, idAgent);
            ps.executeUpdate();
        }
    }

    private void updateCustomer(int idCustomer, String number, String details) throws SQLException {
        String updateCustomer = """
                UPDATE tvcompany.customers
                SET number = ?, details = ?
                WHERE id_customers = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(updateCustomer)) {
            ps.setString(1, number);
            ps.setString(2, details);
            ps.setInt(3, idCustomer);
            ps.executeUpdate();
        }
    }

    public Integer getCustomerIdByLoginAndPassword(String login, String password) throws SQLException {
        auth_handler handler = new auth_handler();
        String hashedLogin = handler.hashString(login);
        String hashedPassword = handler.hashString(password);

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


    public Object[][] get_user_orders(int idCustomer) throws SQLException {
        String sql = """
            SELECT
                ag.full_name,
                ag.mail,
                c.number,
                c.details,
                dt.id_broadcast,
                dt.id_advertising,
                a.summ,
                a.agent_percentage,
                dt.`date`,
                dt.`time`
            FROM tvcompany.agreement a
            INNER JOIN tvcompany.agents ag
                ON a.id_agent = ag.id_agent
            INNER JOIN tvcompany.customers c
                ON a.id_customer = c.id_customers
            INNER JOIN tvcompany.date_time dt
                ON a.id_agreement = dt.id_agreement
            WHERE a.id_customer = ?
            ORDER BY dt.`date`, dt.`time`
            """;

        List<Object[]> rows = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getObject("full_name"),
                            rs.getObject("mail"),
                            rs.getObject("number"),
                            rs.getObject("details"),
                            rs.getObject("id_broadcast"),
                            rs.getObject("id_advertising"),
                            rs.getObject("summ"),
                            rs.getObject("agent_percentage"),
                            rs.getObject("date"),
                            rs.getObject("time")
                    });
                }
            }
        }

        return rows.toArray(new Object[0][]);
    }

    private int createCustomer(String number, String details) throws SQLException {
        String insertCustomer = """
                INSERT INTO tvcompany.customers
                    (number, details)
                VALUES
                    (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                insertCustomer,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, number);
            ps.setString(2, details);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Не удалось создать заказчика");
    }

    private int createAgreement(
            int idCustomer,
            int idAgent,
            BigDecimal summ,
            BigDecimal agentPercentage
    ) throws SQLException {

        String insertAgreement = """
                INSERT INTO tvcompany.agreement
                    (id_customer, id_agent, summ, agent_percentage)
                VALUES
                    (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                insertAgreement,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setInt(1, idCustomer);
            ps.setInt(2, idAgent);
            ps.setBigDecimal(3, summ);
            ps.setBigDecimal(4, agentPercentage);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Не удалось создать договор");
    }

    private void createDateTime(
            int idBroadcast,
            int idAgreement,
            int idAdvertising,
            String date,
            String time
    ) throws SQLException {

        String insertDateTime = """
                INSERT INTO tvcompany.date_time
                    (id_broadcast, id_agreement, id_advertising, `date`, `time`)
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(insertDateTime)) {
            ps.setInt(1, idBroadcast);
            ps.setInt(2, idAgreement);
            ps.setInt(3, idAdvertising);
            ps.setDate(4, Date.valueOf(date.trim()));
            ps.setTime(5, parseSqlTime(time));

            ps.executeUpdate();
        }
    }

    private Time parseSqlTime(String time) {
        String normalizedTime = time.trim();

        if (normalizedTime.matches("\\d{2}:\\d{2}")) {
            normalizedTime += ":00";
        }

        return Time.valueOf(normalizedTime);
    }
}
