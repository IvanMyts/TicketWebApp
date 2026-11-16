package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
import dto.request.LocationRequest;
import dto.response.LocationResponse;
import dto.request.PageQuery;
import dto.response.PageResponse;
import exception.ConflictException;
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
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    public PageResponse<LocationResponse> list(PageQuery query) {
        List<LocationResponse> content = locationRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getSize(), locationRepository.count(query));
    }

    public LocationResponse get(long id) {
        return mapper.toResponse(findEntity(id));
    }

    public LocationResponse create(LocationRequest request) {
        Location location = mapper.toEntity(request);
        locationRepository.save(location);
        changed(location.getId());
        return mapper.toResponse(location);
    }

    public LocationResponse update(long id, LocationRequest request) {
        Location location = findEntity(id);
        mapper.update(location, request);
        changed(id);
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
            Location replacement = findEntity(replacementId);
            persons.forEach(person -> person.setLocation(replacement));
        }
        locationRepository.delete(location);
        changed(id);
    }

    public Location findEntity(long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Локация с id " + id + " не найдена"));
    }

    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.LOCATION, id));
    }
}
