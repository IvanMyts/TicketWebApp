package repository;

import model.Person;

import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class PersonRepository extends BaseRepository<Person> {
    private static final String SELECT = "SELECT e FROM Person e LEFT JOIN FETCH e.location";

    public PersonRepository() {
        super(Person.class, SELECT, Arrays.asList("eyeColor", "hairColor", "nationality"), Arrays.asList("height"));
    }
}
