import ui.CheckWin;
import database.admin_handler;

import java.sql.*;

void main() throws SQLException {
//1-8
    Connection connection = DriverManager.getConnection(
      "jdbc:mysql://127.0.0.1:3306/tvcompany",
            "root",
            "12345678"

    );

    //ui.Window frame = new ui.Window();
    CheckWin check = new CheckWin();

}
