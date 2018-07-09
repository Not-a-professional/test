import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class jdbc {
    public Connection getConnection() {
        String url="jdbc:mysql://localhost:3306/user?useSSL=false";
        String username="root";
        String password="wwwliwei001com";
        String driver = "com.mysql.jdbc.Driver";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.getLocalizedMessage();
        } catch (SQLException e) {
            e.getLocalizedMessage();
        }
        System.out.println("系统启动！");
        return conn;
    }

    public void add() {

    }

    public void delete() {

    }

    public void modify() {

    }

    public void search() {

    }
}
