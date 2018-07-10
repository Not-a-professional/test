import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;


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
        while (checkUsername(username, connection) == -1) {
            System.out.print("用户名重复，请重新输入：");
            username = scanner.next();
        }
        System.out.print("请输入日期(格式yyyy-MM-dd)：");
        String date = scanner.next();
        System.out.print("请输入年龄：");
        int age = scanner.nextInt();
        InputStream in = null;
        System.out.print("请输入图片路径：");
        String address = scanner.next();
        try {
            in = new FileInputStream(address);
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        }
        byte[] buffer = null;
        try {
            if (in != null) {
                buffer = new byte[in.available()];
                in.read(buffer);
                in.close();
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        String sql = "insert into user values(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setBytes(2, buffer);
            try {
                preparedStatement.setTimestamp(3,new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
            } catch (ParseException e) {
                System.out.println(e.getLocalizedMessage());
            }
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

    public void delete(Scanner scanner, Connection connection) {
        System.out.print("请输入需要删除的用户的用户名：");
        String username = scanner.next();
        while (checkUsername(username, connection) == 0) {
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
        System.out.print("请输入需要修改的用户的用户名：");
        String username = scanner.next();
        while(checkUsername(username, connection) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }

        ResultSet rs = null;
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
                try {
                    newDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(temp).getTime());
                } catch (ParseException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            } else if (field.equals("年龄")) {
                System.out.print("输入新的年龄：");
                newAge = scanner.nextInt();
            } else if (field.equals("图片")) {
                System.out.print("输入新的图片路径：");
                String newAddress = scanner.next();
                InputStream in = null;
                try {
                    in = new FileInputStream(newAddress);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                try {
                    if (in != null) {
                        newBuffer = new byte[in.available()];
                        in.read(newBuffer);
                        in.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
            System.out.print("请输入需要修改的字段名(输入保存后即可修改)：");
            field = scanner.next();
        }
        sql = "update user set pic = ? , date = ? , age = ? where username = ?";
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

    public void search(Scanner scanner, Connection connection) {
        System.out.print("请输入所要查找的用户名(支持模糊搜索)：");
        String username = scanner.next();
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

    public int checkUsername(String username, Connection connection) {
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
}
