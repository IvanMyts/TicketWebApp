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
        String jpql = "SELECT e FROM User e WHERE e.login = :login";
        List<User> found = em.createQuery(jpql, User.class).setParameter("login", login).getResultList();
        if (found.isEmpty()) {
            return null;
        }
        return found.get(0);
    }

    public void save(User user) {
        em.persist(user);
    }
}
