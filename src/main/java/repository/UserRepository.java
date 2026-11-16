package repository;

import model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    EntityManager em;

    public Optional<User> findByLogin(String login) {
        return em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getResultList().stream().findFirst();
    }

    public void save(User user) {
        em.persist(user);
    }
}
