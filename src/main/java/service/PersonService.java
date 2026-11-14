package service;

import dto.PageQuery;
import dto.PageResponse;
import dto.PersonRequest;
import dto.PersonResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;
import mapper.PersonMapper;
import model.Person;
import model.Ticket;
import repository.PersonRepository;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class PersonService {
    @Inject
    PersonRepository personRepository;
    @Inject
    TicketRepository ticketRepository;
    @Inject
    LocationService locationService;
    @Inject
    PersonMapper mapper;

    public PageResponse<PersonResponse> list(PageQuery query) {
        List<PersonResponse> content = personRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getPage(), query.getSize(), personRepository.count(query));
    }

    public PersonResponse get(long id) {
        return personRepository.findDetailedById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    public PersonResponse create(PersonRequest request) {
        return mapper.toResponse(createEntity(request));
    }

    public PersonResponse update(long id, PersonRequest request) {
        Person person = findEntity(id);
        mapper.update(person, request);
        person.setLocation(locationService.resolve(request.getLocationId(), request.getLocation()));
        return mapper.toResponse(person);
    }

    /** Билеты этого человека перепривязываются к replacementId, затем человек удаляется. */
    public void delete(long id, Long replacementId) {
        Person person = findEntity(id);
        List<Ticket> tickets = ticketRepository.findByPerson(person);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Человек используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            if (replacementId == id) {
                throw new InvalidRequestException("Замена должна отличаться от удаляемого человека");
            }
            Person replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setPerson(replacement));
        }
        personRepository.delete(person);
    }

    public Person findEntity(long id) {
        return personRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    /** Связь при создании/изменении билета: существующий человек по id, новый из запроса или null. */
    public Person resolve(Long id, PersonRequest request) {
        if (id != null) {
            return findEntity(id);
        }
        return request != null ? createEntity(request) : null;
    }

    private Person createEntity(PersonRequest request) {
        Person person = mapper.toEntity(request);
        person.setLocation(locationService.resolve(request.getLocationId(), request.getLocation()));
        personRepository.save(person);
        return person;
    }

    private ObjectNotFoundException notFound(long id) {
        return new ObjectNotFoundException("Человек с id " + id + " не найден");
    }
}
