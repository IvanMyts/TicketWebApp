package service;

import dto.LocationRequest;
import dto.LocationResponse;
import dto.PageQuery;
import dto.PageResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;
import mapper.LocationMapper;
import model.Location;
import model.Person;
import repository.LocationRepository;
import repository.PersonRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class LocationService {
    @Inject
    LocationRepository locationRepository;
    @Inject
    PersonRepository personRepository;
    @Inject
    LocationMapper mapper;

    public PageResponse<LocationResponse> list(PageQuery query) {
        List<LocationResponse> content = locationRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getPage(), query.getSize(), locationRepository.count(query));
    }

    public LocationResponse get(long id) {
        return mapper.toResponse(findEntity(id));
    }

    public LocationResponse create(LocationRequest request) {
        return mapper.toResponse(createEntity(request));
    }

    public LocationResponse update(long id, LocationRequest request) {
        Location location = findEntity(id);
        mapper.update(location, request);
        return mapper.toResponse(location);
    }

    /** Люди с этой локацией перепривязываются к replacementId, затем локация удаляется. */
    public void delete(long id, Long replacementId) {
        Location location = findEntity(id);
        List<Person> persons = personRepository.findByLocation(location);
        if (!persons.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Локация используется (людей: " + persons.size() + "). Выберите замену.");
            }
            if (replacementId == id) {
                throw new InvalidRequestException("Замена должна отличаться от удаляемой локации");
            }
            Location replacement = findEntity(replacementId);
            persons.forEach(person -> person.setLocation(replacement));
        }
        locationRepository.delete(location);
    }

    public Location findEntity(long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Локация с id " + id + " не найдена"));
    }

    /** Связь при создании/изменении другого объекта: существующая по id, новая из запроса или null. */
    public Location resolve(Long id, LocationRequest request) {
        if (id != null) {
            return findEntity(id);
        }
        return request != null ? createEntity(request) : null;
    }

    private Location createEntity(LocationRequest request) {
        Location location = mapper.toEntity(request);
        locationRepository.save(location);
        return location;
    }
}
