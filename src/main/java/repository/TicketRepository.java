package repository;

import exception.InvalidRequestException;
import model.Ticket;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository extends BaseRepository<Ticket> {
    private static final String SELECT = "SELECT e FROM Ticket e"
            + " LEFT JOIN FETCH e.person p LEFT JOIN FETCH p.location"
            + " LEFT JOIN FETCH e.event LEFT JOIN FETCH e.venue";

    public TicketRepository() {
        super("Ticket", SELECT);
    }

    @Override
    protected Ticket cast(Object row) {
        return (Ticket) row;
    }

    @Override
    protected String textColumn(String column) {
        switch (column) {
            case "name":
                return "e.name";
            case "type":
                return "CAST(e.type AS string)";
            default:
                throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
    }

    public List<Ticket> findReferencing(String relation, Object entity) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t." + relation + " = :entity", Ticket.class)
                .setParameter("entity", entity)
                .getResultList();
    }

    public long countByNumberLessThan(double number) {
        return em.createQuery("SELECT COUNT(t) FROM Ticket t WHERE t.number < :number", Long.class)
                .setParameter("number", number)
                .getSingleResult();
    }

    public List<Ticket> findByNameStartingWith(String prefix) {
        return em.createQuery(SELECT + " WHERE LOCATE(:prefix, e.name) = 1 ORDER BY e.id", Ticket.class)
                .setParameter("prefix", prefix)
                .getResultList();
    }

    public List<Double> findDistinctNumbers() {
        return em.createQuery("SELECT DISTINCT t.number FROM Ticket t WHERE t.number IS NOT NULL ORDER BY t.number",
                        Double.class)
                .getResultList();
    }
}
