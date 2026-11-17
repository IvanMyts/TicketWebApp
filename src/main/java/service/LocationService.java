package service;

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
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class LocationService {
    @Inject
    LocationRepository locationRepository;
    @Inject
    PersonRepository personRepository;

    public PageResponse<LocationResponse> list(PageQuery query) {
        List<LocationResponse> content = new ArrayList<>();
        for (Location item : locationRepository.findPage(query)) {
            content.add(LocationMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), locationRepository.count(query));
    }

    public LocationResponse get(long id) {
        return LocationMapper.toResponse(findEntity(id));
    }

    public LocationResponse create(LocationRequest request) {
        Location location = LocationMapper.toEntity(request);
        locationRepository.save(location);
        return LocationMapper.toResponse(location);
    }

    public LocationResponse update(long id, LocationRequest request) {
        Location location = findEntity(id);
        LocationMapper.update(location, request);
        return LocationMapper.toResponse(location);
    }

    public void delete(long id, Long replacementId) {
        Location location = findEntity(id);
        List<Person> persons = personRepository.findByLocation(location);
        if (!persons.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Локация используется (людей: " + persons.size() + "). Выберите замену.");
            }
            Location replacement = findEntity(replacementId);
            for (Person person : persons) {
                person.setLocation(replacement);
            }
        }
        locationRepository.delete(location);
    }

    public Location findEntity(long id) {
        Location found = locationRepository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Локация с id " + id + " не найдена");
        }
        return found;
    }
}
