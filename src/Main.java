import org.hibernate.Session;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while(true) {
            System.out.println("1：JDBC");
            System.out.println("2：Hibernate");
            System.out.println("0：退出");
            int i = scanner.nextInt();
            switch (i) {
                case 1:
                    test2(scanner);
                    break;
                case 2:
                    test3(scanner);
                    break;
                 case 0:
                     System.exit(0);
            }
        }
    }

    public static void test3(Scanner scanner) {
        Session session = Hibernate.getSession();

        session.beginTransaction();
        Hbn j = new Hbn();
        boolean flag = true;
        do {
            System.out.println("1：增加数据");
            System.out.println("2：删除数据");
            System.out.println("3：修改数据");
            System.out.println("4：查询数据");
            System.out.println("0：退出程序");
            int i = scanner.nextInt();
            switch (i) {
                case 1:
                    j.add(scanner, session);
                    break;
                case 2:
                    j.delete(scanner, session);
                    break;
                case 3:
                    j.modify(scanner, session);
                    break;
                case 4:
                    j.search(scanner, session);
                    break;
                case 0:
                    Hibernate.closeSession();
                    Hibernate.shutdown();
                    flag = false;
                    break;
            }
        } while(flag);
    }

    public static void test2(Scanner scanner) {
        jdbc j = new jdbc();
        Connection connection = j.getConnection();
        boolean flag = true;
        do {
            System.out.println("1：增加数据");
            System.out.println("2：删除数据");
            System.out.println("3：修改数据");
            System.out.println("4：查询数据");
            System.out.println("0：退出程序");
            int i = scanner.nextInt();
            switch (i) {
                case 1:
                    j.add(scanner, connection);
                    break;
                case 2:
                    j.delete(scanner, connection);
                    break;
                case 3:
                    j.modify(scanner, connection);
                    break;
                case 4:
                    j.search(scanner, connection);
                    break;
                case 0:
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.getLocalizedMessage();
                    }
                    flag = false;
                    break;
            }
        } while(flag);
    }

    public void test1() {
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            Long num = scanner.nextLong();
            if (num.toString().length() > 12) {
                System.out.print("输入长度不能超过12位\n");
            } else {
                if (num.toString().length() <= 4) {
                    result(num, 1, true, true);
                } else {
                    result(num, 1, false, false);
                }
                System.out.print("\n");
            }
        }
    }

    private static void result(Long a, int depth, boolean start, boolean four) {
        if (a.toString().length() > 4 && !start) {
            if (Long.valueOf(a / 10000).toString().length() <= 4) {
                result(a / 10000, depth + 1, true, four);
            } else {
                result(a / 10000, depth + 1, false, four);
            }
        } else if(a.toString().length() <= 4 && !start) {
            result(a, depth + 1, true, four);
        }
        long temp = a % 10000;
        if (temp != 0) {
            if (depth == 1 && a.toString().length() > 8
                    && a / 10000 % 10000 == 0 && (temp / 1000) != 0) {
                System.out.print('零');
            }
            boolean flag = false;
            for (int i = 3; i >= 0; --i) {
                long t = temp / power(i);
                if (t == 0) {
                    if (i == 3) {
                        if (!start) {
                            System.out.print('零');
                            flag = !flag;
                        }
                    }
                    if ((!flag && i != 3 && !start) || (four && flag)) {
                        System.out.print('零');
                        flag = !flag;
                    }
                } else {
                    System.out.print(getChar(t));
                    if (i != 0) {
                        System.out.print(getDan(i));
                    }
                    flag = !flag;
                }
                temp = temp % power(i);
                if (temp == 0) {
                    break;
                }
            }
            if (depth == 2) {
                System.out.print('万');
            } else if (depth == 3) {
                System.out.print('亿');
            }
        } else if (start) {
            System.out.print('零');
        }
    }

    private static long power(int i) {
        long temp = 1;
        for (;i > 0; --i) {
            temp *= 10;
        }
        return temp;
    }

    private static char getChar(long i) {
        if (i == 0) {
            return '零';
        } else if (i == 1) {
            return '一';
        } else if (i == 2) {
            return '二';
        } else if (i == 3) {
            return '三';
        } else if (i == 4) {
            return '四';
        } else if (i == 5) {
            return '五';
        } else if (i == 6) {
            return '六';
        } else if (i == 7) {
            return '七';
        } else if (i == 8) {
            return '八';
        } else if (i == 9) {
            return '九';
        } else {
            return ' ';
        }
    }

    private static char getDan(int i) {
        if (i == 1) {
            return '十';
        } else if (i == 2) {
            return '百';
        } else if (i == 3) {
            return '千';
        } else {
            return ' ';
        }
    }
}