package repository;

import exception.InvalidRequestException;
import model.Location;
import model.Person;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PersonRepository extends BaseRepository<Person, Long> {
    private static final String SELECT = "SELECT p FROM Person p LEFT JOIN FETCH p.location";

    public PersonRepository() {
        super(Person.class, "p", SELECT);
    }

    /** Строковых колонок у Person нет, поэтому используются enum как строки. */
    @Override
    protected String textColumn(String column) {
        return switch (column) {
            case "eyeColor" -> "CAST(p.eyeColor AS string)";
            case "hairColor" -> "CAST(p.hairColor AS string)";
            case "nationality" -> "CAST(p.nationality AS string)";
            default -> throw new InvalidRequestException("Недопустимая колонка: " + column);
        };
    }

    public List<Person> findByLocation(Location location) {
        return em.createQuery("SELECT p FROM Person p WHERE p.location = :location", Person.class)
                .setParameter("location", location)
                .getResultList();
    }
}
