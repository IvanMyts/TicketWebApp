package repository;

import exception.InvalidRequestException;
import model.Location;
import model.Person;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PersonRepository extends BaseRepository<Person> {
    private static final String SELECT = "SELECT e FROM Person e LEFT JOIN FETCH e.location";

    public PersonRepository() {
        super("Person", SELECT);
    }

    @Override
    protected String textColumn(String column) {
        switch (column) {
            case "eyeColor":
                return "CAST(e.eyeColor AS string)";
            case "hairColor":
                return "CAST(e.hairColor AS string)";
            case "nationality":
                return "CAST(e.nationality AS string)";
            default:
                throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
    }

    public List<Person> findByLocation(Location location) {
        return em.createQuery("SELECT p FROM Person p WHERE p.location = :location", Person.class)
                .setParameter("location", location)
                .getResultList();
    }
}
