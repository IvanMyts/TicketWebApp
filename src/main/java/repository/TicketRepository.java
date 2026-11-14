package repository;

import exception.InvalidRequestException;
import model.Event;
import model.Person;
import model.Ticket;
import model.Venue;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

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

    /** Билет со всеми связанными объектами (для окна просмотра). */
    public Optional<Ticket> findDetailedById(long id) {
        return em.createQuery(SELECT + " WHERE t.id = :id", Ticket.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }

    public List<Ticket> findByPerson(Person person) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.person = :person", Ticket.class)
                .setParameter("person", person)
                .getResultList();
    }

    public List<Ticket> findByEvent(Event event) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.event = :event", Ticket.class)
                .setParameter("event", event)
                .getResultList();
    }

    public List<Ticket> findByVenue(Venue venue) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.venue = :venue", Ticket.class)
                .setParameter("venue", venue)
                .getResultList();
    }

    // ---------- Специальные операции ----------

    /** Билеты с number = null не учитываются. */
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

    /** null значением не считается и не возвращается. */
    public List<Double> findDistinctNumbers() {
        return em.createQuery("SELECT DISTINCT t.number FROM Ticket t WHERE t.number IS NOT NULL ORDER BY t.number",
                        Double.class)
                .getResultList();
    }
}
