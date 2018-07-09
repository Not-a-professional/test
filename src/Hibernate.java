import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Hibernate {
    public SessionFactory getSessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }
}
