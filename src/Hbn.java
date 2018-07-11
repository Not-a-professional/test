import model.user;
import org.hibernate.Session;
import service.HibernateService;
import service.JDBCService;

import java.io.*;
import java.util.Scanner;

public class Hbn {
    public void add(Scanner scanner, Session session) {
        user user = new user();

        System.out.print("请输入日期(格式yyyy-MM-dd)：");
        String date = scanner.next();
        HibernateService.checkDate(date, scanner, user);
        System.out.print("请输入图片路径：");
        String address = scanner.next();
        byte[] buffer = JDBCService.uploadBlob(address, scanner);
        user.setPic(buffer);
        System.out.print("请输入用户名：");
        String username = scanner.next();
        while(HibernateService.checkUsername(username, session) == -1) {
            System.out.print("用户名重复，请重新输入：");
            username = scanner.next();
        }
        user.setUsername(username);
        System.out.print("请输入年龄：");
        String s = scanner.next();
        while(!s.matches("[1-9][0-9]+")) {
            System.out.print("年龄格式不合法，请重新输入：");
            s = scanner.next();
        }
        int age = Integer.parseInt(s);
        user.setAge(age);
        session.save(user);
        session.getTransaction().commit();
        System.out.println("增加成功！");
    }

    public void delete(Scanner scanner, Session session) {
        if (HibernateService.isEmpty(session)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入想要删除的用户的用户名：");
        String username = scanner.next();
        while(HibernateService.checkUsername(username, session) == 0) {
            System.out.print("用户不存在，请重新输入：");
            username = scanner.next();
        }

        user user = (model.user) session.get(model.user.class, username);
        session.delete(user);
        session.getTransaction().commit();
        System.out.println("删除成功！");
    }

    public void  modify(Scanner scanner, Session session) {
        if (HibernateService.isEmpty(session)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入需要修改的用户的用户名：");
        String username = scanner.next();
        while(HibernateService.checkUsername(username, session) == 0) {
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
                HibernateService.checkDate(newDate, scanner, user);
            } else if (field.equals("年龄")) {
                System.out.print("输入新的年龄：");
                String s = scanner.next();
                while(!s.matches("[1-9][0-9]+")) {
                    System.out.print("年龄格式不合法，请重新输入：");
                    s = scanner.next();
                }
                int newAge = Integer.parseInt(s);
                user.setAge(newAge);
            } else if (field.equals("图片")) {
                System.out.print("输入新的图片路径：");
                String newAddress = scanner.next();
                byte[] buffer = JDBCService.uploadBlob(newAddress, scanner);
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
        if (HibernateService.isEmpty(session)) {
            System.out.println("暂无数据可操作！");
            return;
        }
        System.out.print("请输入所要查找的用户名(支持模糊搜索)：");
        String username = scanner.next();
        HibernateService.searchByUsername(username, session);
    }
}
