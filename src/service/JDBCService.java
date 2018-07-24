package service;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class JDBCService {

    public static byte[] uploadBlob(String address, Scanner scanner) {
        InputStream in = null;
        boolean flag = false;
        byte[] buffer = null;
        while (!flag) {
            try {
                in = new FileInputStream(address);
            } catch (FileNotFoundException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.print("图片路径不合法或不存在，请重新输入：");
                address = scanner.next();
            }
            try {
                if (in != null) {
                    buffer = new byte[in.available()];
                    in.read(buffer);
                    in.close();
                    flag = true;
                }
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.print("文件读取失败，请重新输入：");
                address = scanner.next();
            }
        }
        return buffer;
    }

    public static int checkUsername(String username, Connection connection) {
        String sql = "select u.* from user u where u.username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                preparedStatement.close();
                return 0;
            } else {
                preparedStatement.close();
                return -1;
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return 0;
    }

    public static boolean isEmpty(Connection connection) {
        String sql = "select u.* from user u";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                preparedStatement.close();
                return true;
            } else {
                preparedStatement.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return true;
    }

    public static void searchByUsername(String username, Connection connection) {
        String sql = "select * from user where username like ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + username + "%");
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                System.out.println("查无此人！");
            } else {
                do {
                    System.out.println("用户名：" + rs.getString(1) + " "
                            + "日期：" + rs.getDate(3) + " "
                            + "年龄：" + rs.getInt(4) + " " + "图片：");
                    OutputStream os = new ByteArrayOutputStream();
                    try {
                        os.write(rs.getBytes(2));
                    } catch (IOException e) {
                        preparedStatement.close();
                        System.out.println(e.getLocalizedMessage());
                    }
                } while(rs.next());
                preparedStatement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("查询失败！");
        }
    }

    public static void add(Connection connection, String username, byte[] buffer, Timestamp date, int age) {
        String sql = "insert into user values(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setBytes(2, buffer);
            preparedStatement.setTimestamp(3, date);
            preparedStatement.setInt(4, age);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("增加失败！");
            return;
        }
        System.out.println("增加成功！");
    }

    public static void update(Connection connection, byte[] newBuffer, Timestamp newDate, int newAge, String newUsername) {
        String sql = "update user set pic = ? , date = ? , age = ? where username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, newBuffer);
            preparedStatement.setTimestamp(2, newDate);
            preparedStatement.setInt(3, newAge);
            preparedStatement.setString(4, newUsername);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("更新失败！");
            return;
        }
        System.out.println("更新成功！");
    }
}
