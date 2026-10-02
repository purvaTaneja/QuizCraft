package util;

import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {

    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("Database connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
