package repository;

import model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    EntityManager em;

    public User findByLogin(String login) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public void save(User user) {
        em.persist(user);
    }
}
