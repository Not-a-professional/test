package service;

import model.User;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class HibernateService {
    public static int checkUsername(String username, Session session) {
        User user = (User) session.createQuery("select u from User u where u.username = ?").setParameter(0, username).uniqueResult();
        if (user != null) {
            return -1;
        } else {
            return 0;
        }
    }

    public static boolean isEmpty(Session session) {
        List<User> users = (List<User>) session.createQuery("select u from User u").list();
        return users.isEmpty();
    }

    public static void searchByUsername(String username, Session session) {
        List<User> users = (List<User>) session.createQuery("select u from User u where u.username like ?")
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

    public static void checkDate(String date, Scanner scanner, User user) {
        boolean flag = false;
        while (!flag) {
            try {
                user.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
                flag = true;
            } catch (ParseException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.print("日期格式不合法，请重新输入：");
                date = scanner.next();
            }
        }
    }
}
