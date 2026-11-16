package mapper;

import dto.request.PersonRequest;
import dto.response.PersonResponse;
import model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Связь с Location здесь не устанавливается: по locationId её нужно найти в БД,
 * а новую (request.location) — создать. Это делает сервис.
 */
@ApplicationScoped
public class PersonMapper {

    @Inject
    private LocationMapper locationMapper;

    public Person toEntity(PersonRequest request) {
        Person person = new Person();
        update(person, request);
        return person;
    }

    /** Полная замена простых полей (семантика PUT). id и location не трогаются. */
    public void update(Person target, PersonRequest request) {
        target.setEyeColor(request.getEyeColor());
        target.setHairColor(request.getHairColor());
        target.setHeight(request.getHeight());
        target.setNationality(request.getNationality());
    }

    public PersonResponse toResponse(Person entity) {
        if (entity == null) {
            return null;
        }
        return new PersonResponse(
                entity.getId(),
                entity.getEyeColor(),
                entity.getHairColor(),
                locationMapper.toResponse(entity.getLocation()),
                entity.getHeight(),
                entity.getNationality()
        );
    }
}
