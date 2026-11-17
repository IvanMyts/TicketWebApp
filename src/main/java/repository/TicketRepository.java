package repository;

import model.Ticket;

import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class TicketRepository extends BaseRepository<Ticket> {
    private static final String SELECT = "SELECT e FROM Ticket e"
            + " LEFT JOIN FETCH e.person p LEFT JOIN FETCH p.location"
            + " LEFT JOIN FETCH e.event LEFT JOIN FETCH e.venue";

    public TicketRepository() {
        super(Ticket.class, SELECT, Arrays.asList("name", "type"),
                Arrays.asList("coordinates.x", "coordinates.y", "creationDate", "price", "discount", "number"));
    }

    public long countByNumberLessThan(double number) {
        String jpql = "SELECT COUNT(e) FROM Ticket e WHERE e.number < :number";
        return em.createQuery(jpql, Long.class).setParameter("number", number).getSingleResult();
    }

    public List<Ticket> findByNameStartingWith(String prefix) {
        String jpql = SELECT + " WHERE LOCATE(:prefix, e.name) = 1 ORDER BY e.id";
        return em.createQuery(jpql, Ticket.class).setParameter("prefix", prefix).getResultList();
    }

    public List<Double> findDistinctNumbers() {
        String jpql = "SELECT DISTINCT e.number FROM Ticket e WHERE e.number IS NOT NULL ORDER BY e.number";
        return em.createQuery(jpql, Double.class).getResultList();
    }
}
