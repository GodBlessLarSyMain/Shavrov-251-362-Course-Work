package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class agent_handler extends db_handler {

    public Object[][] get_ad_broadcast() throws SQLException {
        String request = """
                SELECT 
                    tvcompany.broadcasts.id_broadcasts,
                    tvcompany.broadcasts.rating,
                    tvcompany.date_time.date,
                    tvcompany.date_time.time
                FROM tvcompany.broadcasts
                LEFT JOIN tvcompany.date_time
                    ON tvcompany.date_time.id_broadcast = tvcompany.broadcasts.id_broadcasts
                ORDER BY tvcompany.broadcasts.id_broadcasts
                """;

        ArrayList<Object[]> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(request);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] data = new Object[4];

                data[0] = rs.getInt("id_broadcasts");
                data[1] = rs.getBigDecimal("rating");
                data[2] = rs.getDate("date");
                data[3] = rs.getTime("time");

                result.add(data);
            }
        }

        return result.toArray(new Object[0][]);
    }

    public Object[][] get_agent_data(int id_agent) throws SQLException {
        String request = """
                SELECT 
                    tvcompany.agreement.id_customer,
                    tvcompany.date_time.id_advertising,
                    tvcompany.agreement.summ,
                    tvcompany.agreement.agent_percentage
                FROM tvcompany.agreement
                LEFT JOIN tvcompany.date_time
                    ON tvcompany.date_time.id_agreement = tvcompany.agreement.id_agreement
                WHERE tvcompany.agreement.id_agent = ?
                """;

        ArrayList<Object[]> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(request)) {
            ps.setInt(1, id_agent);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[4];

                    data[0] = rs.getInt("id_customer");
                    data[1] = rs.getInt("id_advertising");
                    data[2] = rs.getBigDecimal("summ");
                    data[3] = rs.getBigDecimal("agent_percentage");

                    result.add(data);
                }
            }
        }

        return result.toArray(new Object[0][]);
    }

    public Object[] get_agent_profile(int idAgent) throws SQLException {
        String sql = """
            SELECT full_name, mail
            FROM tvcompany.agents
            WHERE id_agent = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAgent);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getString("full_name"),
                            rs.getString("mail")
                    };
                }
            }
        }

        return new Object[]{"", ""};
    }

    public void update_agent_profile(int idAgent, String fullName, String mail) throws SQLException {
        String sql = """
            UPDATE tvcompany.agents
            SET full_name = ?, mail = ?
            WHERE id_agent = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, mail);
            ps.setInt(3, idAgent);

            ps.executeUpdate();
        }
    }
}