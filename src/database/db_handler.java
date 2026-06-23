package database;

import java.sql.*;
import java.util.ArrayList;


public class db_handler {

    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/tvcompany",
                    "root",
                    "12345678"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Передача
    public void create_broadcast(int id, double rating) throws SQLException {
        String sql_request = """
                INSERT INTO tvcompany.broadcasts 
                (id_broadcasts, rating) 
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.setDouble(2, rating);
            ps.executeUpdate();
        }
    }

    public Object[][] get_broadcast() throws SQLException {
        String sql_request = """
                SELECT * 
                FROM tvcompany.broadcasts 
                """;

        ArrayList<Object[]> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql_request);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] data = new Object[2];

                data[0] = rs.getInt("id_broadcasts");
                data[1] = rs.getBigDecimal("rating");
                result.add(data);
            }
        }

        return result.toArray(new Object[0][]);
        }


    public void delete_broadcast(int id) throws SQLException {
        String sql_request = """
                DELETE FROM tvcompany.broadcasts 
                WHERE id_broadcasts = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    //Реклама
    public void create_ad(int id, int minuteCost, String duration) throws SQLException {
        String sql_request = """
                INSERT INTO tvcompany.advertisings 
                (id_advertisings, minute_cost, duration) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.setInt(2, minuteCost);
            ps.setTime(3, Time.valueOf(duration));
            ps.executeUpdate();
        }
    }

    public void get_ad(int id) throws SQLException {
        String sql_request = """
                SELECT * 
                FROM tvcompany.advertisings 
                WHERE id_advertisings = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID рекламы: " + rs.getInt("id_advertisings"));
                    System.out.println("Цена за минуту: " + rs.getInt("minute_cost"));
                    System.out.println("Продолжительность: " + rs.getTime("duration"));
                }
            }
        }
    }

    public void delete_ad(int id) throws SQLException {
        String sql_request = """
                DELETE FROM tvcompany.advertisings 
                WHERE id_advertisings = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    //Дата/время
    public void create_dt(
            int id,
            int idBroadcast,
            int idAgreement,
            int idAdvertising,
            String date,
            String time
    ) throws SQLException {

        String sql_request = """
                INSERT INTO tvcompany.date_time 
                (id_date_time, id_broadcast, id_agreement, id_advertising, date, time) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.setInt(2, idBroadcast);
            ps.setInt(3, idAgreement);
            ps.setInt(4, idAdvertising);
            ps.setDate(5, Date.valueOf(date));
            ps.setTime(6, Time.valueOf(time));

            ps.executeUpdate();
        }
    }

    public void get_dt(int id) throws SQLException {
        String sql_request = """
                SELECT * 
                FROM tvcompany.date_time 
                WHERE id_date_time = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID дата/время: " + rs.getInt("id_date_time"));
                    System.out.println("ID передачи: " + rs.getInt("id_broadcast"));
                    System.out.println("ID рекламы: " + rs.getInt("id_advertising"));
                    System.out.println("ID договора: " + rs.getInt("id_agreement"));
                    System.out.println("Дата: " + rs.getDate("date"));
                    System.out.println("Время: " + rs.getTime("time"));
                }
            }
        }
    }

    public void delete_dt(int id) throws SQLException {
        String sql_request = """
                DELETE FROM tvcompany.date_time 
                WHERE id_date_time = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql_request)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Object[][] get_agents() throws SQLException {
        String sql_request = """
                SELECT * 
                FROM tvcompany.agents 
                """;

        ArrayList<Object[]> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql_request);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] data = new Object[2];

                data[0] = rs.getString("full_name");
                data[1] = rs.getString("mail");
                result.add(data);
            }
        }

        return result.toArray(new Object[0][]);
    }
}