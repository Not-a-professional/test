import jdk.nashorn.internal.scripts.JD;
import service.JDBCService;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;


class jdbc {
    public Connection getConnection() {
        String url="jdbc:mysql://localhost:3306/user?useSSL=false&characterEncoding=UTF-8";
        String username="root";
        String password="wwwliwei001com";
        String driver = "com.mysql.jdbc.Driver";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println("系统启动！");
        return conn;
    }

    public void add(Scanner scanner, Connection connection) {
        System.out.print("请输入用户名：");
        String username = scanner.next();
        while (JDBCService.checkUsername(username, connection) == -1) {
            System.out.print("用户名重复，请重新输入：");
            username = scanner.next();
        }
        System.out.print("请输入日期(格式yyyy-MM-dd)：");
        String d = scanner.next();
        boolean flag = false;
        Timestamp date = null;
        while(!flag) {
            try {
                date = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(d).getTime());
                flag = true;
            } catch (ParseException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.println("日期输入不合法，请重新输入：");
                d = scanner.next();
            }
        }
        System.out.print("请输入年龄：");
        String s = scanner.next();
        while(!s.matches("[1-9][0-9]+")) {
            System.out.print("年龄格式不合法，请重新输入：");
            s = scanner.next();
        }
        int age = Integer.parseInt(s);
        System.out.print("请输入图片路径：");
        String address = scanner.next();
        byte[] buffer = JDBCService.uploadBlob(address, scanner);
        JDBCService.add(connection, username, buffer, date, age);
    }

    public void delete(Scanner scanner, Connection connection) {
        if (JDBCService.isEmpty(connection)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入需要删除的用户的用户名：");
        String username = scanner.next();
        while (JDBCService.checkUsername(username, connection) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }
        String sql = "delete from user where username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("删除失败！");
            return;
        }
        System.out.println("删除成功！");
    }

    public void modify(Scanner scanner, Connection connection) {
        if (JDBCService.isEmpty(connection)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入需要修改的用户的用户名：");
        String username = scanner.next();
        while(JDBCService.checkUsername(username, connection) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }

        ResultSet rs;
        String sql = "select u.* from user u where u.username = ?";
        String newUsername = username;
        byte[] newBuffer = null;
        Timestamp newDate = null;
        int newAge = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            rs = preparedStatement.executeQuery();
            rs.next();
            newUsername = rs.getString(1);
            newBuffer = rs.getBytes(2);
            newDate = rs.getTimestamp(3);
            newAge = rs.getInt(4);
            System.out.println("用户名：" + newUsername + " " + "日期：" + newDate + " " + "年龄："
                    + newAge + " " + "图片：");
            OutputStream os = new ByteArrayOutputStream();
            try {
                os.write(newBuffer);
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.print("请输入需要修改的字段名(输入保存后即可修改)：");
        String field = scanner.next();
        while (!field.equals("保存")) {
            if (field.equals("日期")) {
                System.out.print("输入新的日期(格式yyyy-MM-dd)：");
                String temp = scanner.next();
                boolean flag = false;
                while (!flag) {
                    try {
                        newDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(temp).getTime());
                        flag = true;
                    } catch (ParseException e) {
                        System.out.println(e.getLocalizedMessage());
                        System.out.print("日期格式不合法，请重新输入：");
                        temp = scanner.next();
                    }
                }
            } else if (field.equals("年龄")) {
                System.out.print("输入新的年龄：");
                String s = scanner.next();
                while(!s.matches("[1-9][0-9]+")) {
                    System.out.print("年龄格式不合法，请重新输入：");
                    s = scanner.next();
                }
                newAge = Integer.parseInt(s);
            } else if (field.equals("图片")) {
                System.out.print("输入新的图片路径：");
                String newAddress = scanner.next();
                newBuffer = JDBCService.uploadBlob(newAddress, scanner);
            }
            System.out.print("请输入需要修改的字段名(输入保存后即可修改)：");
            field = scanner.next();
        }
        JDBCService.update(connection, newBuffer, newDate, newAge ,newUsername);
    }

    public void search(Scanner scanner, Connection connection) {
        if (JDBCService.isEmpty(connection)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入所要查找的用户名(支持模糊搜索)：");
        String username = scanner.next();
        JDBCService.searchByUsername(username, connection);
    }
}
