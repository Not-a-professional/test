import model.user;
import org.hibernate.Session;

import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Hbm {
    public void add(Scanner scanner, Session session) {
        user user = new user();

        System.out.print("请输入日期(格式yyyy-MM-dd)：");
        String date = scanner.next();
        try {
            user.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
        }
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
        user.setPic(buffer);
        System.out.print("请输入用户名：");
        String username = scanner.next();
        while(checkUsername(username, session) == -1) {
            System.out.print("用户名重复，请重新输入：");
            username = scanner.next();
        }
        user.setUsername(username);
        System.out.print("请输入年龄：");
        int age = scanner.nextInt();
        user.setAge(age);
        session.save(user);
        session.getTransaction().commit();
        System.out.println("增加成功！");
    }

    public void delete(Scanner scanner, Session session) {
        System.out.print("请输入想要删除的用户的用户名：");
        String username = scanner.next();
        while(checkUsername(username, session) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }

        user user = (model.user) session.get(model.user.class, username);
        session.delete(user);
        session.getTransaction().commit();
        System.out.println("删除成功！");
    }

    public void  modify(Scanner scanner, Session session) {
        System.out.print("请输入需要修改的用户的用户名：");
        String username = scanner.next();
        while(checkUsername(username, session) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }

        user user = (model.user) session.get(model.user.class, username);

        System.out.println("日期：" + user.getDate() + " " + "年龄：" + user.getAge() + " " + "图片：");
        OutputStream os = new ByteArrayOutputStream();
        try {
            os.write(user.getPic());
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.print("请输入需要修改的字段名(输入保存后即可修改)：");
        String field = scanner.next();
        while(!field.equals("保存")) {
            if (field.equals("日期")) {
                System.out.print("输入新的日期(格式yyyy-MM-dd)：");
                String newDate = scanner.next();
                try {
                    user.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(newDate).getTime()));
                } catch (ParseException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            } else if (field.equals("年龄")) {
                System.out.print("输入新的年龄：");
                int newAge = scanner.nextInt();
                user.setAge(newAge);
            } else if (field.equals("图片")) {
                System.out.print("输入新的图片路径：");
                String newAddress = scanner.next();
                InputStream in = null;
                try {
                    in = new FileInputStream(newAddress);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getLocalizedMessage());;
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
                user.setPic(buffer);
            }
            System.out.print("请输入需要修改的字段名(输入保存后即可修改)：");
            field = scanner.next();
        }

        session.update(user);
        session.getTransaction().commit();
        System.out.println("修改成功！");
    }

    public void search(Scanner scanner, Session session) {
        System.out.print("请输入所要查找的用户名(支持模糊搜索)：");
        String username = scanner.next();
        List<user> users = (List<user>) session.createQuery("select u from user u where u.username like ?")
                .setParameter(0, "%" + username + "%").list();
        if (users.isEmpty()) {
            System.out.println("查无此人！");
        } else {
            for(int i = 0;i < users.size(); ++i) {
                System.out.println("用户名：" + users.get(i).getUsername() + " "
                        + "日期：" + users.get(i).getDate() + " "
                        + "年龄：" + users.get(i).getAge() + " "
                        + "图片：");
                OutputStream os = new ByteArrayOutputStream();
                try {
                    os.write(users.get(i).getPic());
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }

    public int checkUsername(String username, Session session) {
        user user = (user) session.createQuery("select u from user u where u.username = ?").setParameter(0, username).uniqueResult();
        if (user != null) {
            return -1;
        } else {
            return 0;
        }
    }
}
