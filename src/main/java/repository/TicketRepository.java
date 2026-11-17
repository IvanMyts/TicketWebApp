package repository;

import exception.InvalidRequestException;
import model.Ticket;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository extends BaseRepository<Ticket, Long> {
    private static final String SELECT = "SELECT t FROM Ticket t"
            + " LEFT JOIN FETCH t.person p LEFT JOIN FETCH p.location"
            + " LEFT JOIN FETCH t.event LEFT JOIN FETCH t.venue";

    public TicketRepository() {
        super(Ticket.class, "t", SELECT);
    }

    @Override
    protected String textColumn(String column) {
        return switch (column) {
            case "name" -> "t.name";
            case "type" -> "CAST(t.type AS string)";
            default -> throw new InvalidRequestException("Недопустимая колонка: " + column);
        };
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
        return em.createQuery(SELECT + " WHERE LOCATE(:prefix, t.name) = 1 ORDER BY t.id", Ticket.class)
                .setParameter("prefix", prefix)
                .getResultList();
    }

    public List<Double> findDistinctNumbers() {
        return em.createQuery("SELECT DISTINCT t.number FROM Ticket t WHERE t.number IS NOT NULL ORDER BY t.number",
                        Double.class)
                .getResultList();
    }
}
