package database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class admin_handler extends db_handler {

    public Object[][] get_admin_data() throws SQLException {
        String sql_request = """
            SELECT 
                tvcompany.date_time.id_date_time,
                tvcompany.broadcasts.id_broadcasts,
                tvcompany.advertisings.id_advertisings,
                tvcompany.agents.id_agent,
                tvcompany.agents.mail,
                tvcompany.agreement.id_agreement,
                tvcompany.agreement.summ,
                tvcompany.date_time.date,
                tvcompany.date_time.time
            FROM tvcompany.date_time
            JOIN tvcompany.agreement 
                ON tvcompany.date_time.id_agreement = tvcompany.agreement.id_agreement
            JOIN tvcompany.broadcasts 
                ON tvcompany.date_time.id_broadcast = tvcompany.broadcasts.id_broadcasts
            JOIN tvcompany.advertisings 
                ON tvcompany.date_time.id_advertising = tvcompany.advertisings.id_advertisings
            JOIN tvcompany.agents 
                ON tvcompany.agreement.id_agent = tvcompany.agents.id_agent
            ORDER BY tvcompany.date_time.id_date_time
            """;

        ArrayList<Object[]> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql_request);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] data = new Object[9];

                data[0] = rs.getInt("id_date_time");
                data[1] = rs.getInt("id_broadcasts");
                data[2] = rs.getInt("id_advertisings");
                data[3] = rs.getInt("id_agent");
                data[4] = rs.getString("mail");
                data[5] = rs.getInt("id_agreement");
                data[6] = rs.getBigDecimal("summ");
                data[7] = rs.getDate("date");
                data[8] = rs.getTime("time");

                result.add(data);
            }
        }

        return result.toArray(new Object[0][]);
    }

    public void update_admin_data(
        int idDateTime,
        int idBroadcast,
        int idAdvertising,
        int idAgent,
        String mail,
        int idAgreement,
        BigDecimal summ,
        String date,
        String time
    ) throws SQLException {

            String updateDateTime = """
                UPDATE tvcompany.date_time
                SET 
                    id_broadcast = ?,
                    id_advertising = ?,
                    id_agreement = ?,
                    `date` = ?,
                    `time` = ?
                WHERE id_date_time = ?
                """;

            String updateAgreement = """
                UPDATE tvcompany.agreement
                SET 
                    id_agent = ?,
                    summ = ?
                WHERE id_agreement = ?
                """;

            String updateAgent = """
                UPDATE tvcompany.agents
                SET mail = ?
                WHERE id_agent = ?
                """;

            boolean oldAutoCommit = connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                try (PreparedStatement ps = connection.prepareStatement(updateAgreement)) {
                    ps.setInt(1, idAgent);
                    ps.setBigDecimal(2, summ);
                    ps.setInt(3, idAgreement);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = connection.prepareStatement(updateAgent)) {
                    ps.setString(1, mail);
                    ps.setInt(2, idAgent);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = connection.prepareStatement(updateDateTime)) {
                    ps.setInt(1, idBroadcast);
                    ps.setInt(2, idAdvertising);
                    ps.setInt(3, idAgreement);
                    ps.setDate(4, Date.valueOf(date));   // формат: 2026-06-23
                    ps.setTime(5, Time.valueOf(time));   // формат: 15:00:00
                    ps.setInt(6, idDateTime);
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


}