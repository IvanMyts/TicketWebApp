package mapper;

import dto.request.PersonRequest;
import dto.response.PersonResponse;
import model.Person;

public class PersonMapper {

    public static Person toEntity(PersonRequest request) {
        Person person = new Person();
        update(person, request);
        return person;
    }

    public static void update(Person target, PersonRequest request) {
        target.setEyeColor(request.getEyeColor());
        target.setHairColor(request.getHairColor());
        target.setHeight(request.getHeight());
        target.setNationality(request.getNationality());
    }

    public static PersonResponse toResponse(Person entity) {
        if (entity == null) {
            return null;
        }
        return new PersonResponse(
                entity.getId(),
                entity.getEyeColor(),
                entity.getHairColor(),
                LocationMapper.toResponse(entity.getLocation()),
                entity.getHeight(),
                entity.getNationality()
        );
    }
}
