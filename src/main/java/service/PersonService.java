package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.PersonRequest;
import dto.response.PersonResponse;
import exception.ConflictException;
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
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    public PageResponse<PersonResponse> list(PageQuery query) {
        List<PersonResponse> content = personRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getSize(), personRepository.count(query));
    }

    public PersonResponse get(long id) {
        return mapper.toResponse(findEntity(id));
    }

    public PersonResponse create(PersonRequest request) {
        Person person = mapper.toEntity(request);
        person.setLocation(request.getLocationId() == null ? null : locationService.findEntity(request.getLocationId()));
        personRepository.save(person);
        changed(person.getId());
        return mapper.toResponse(person);
    }

    public PersonResponse update(long id, PersonRequest request) {
        Person person = findEntity(id);
        mapper.update(person, request);
        person.setLocation(request.getLocationId() == null ? null : locationService.findEntity(request.getLocationId()));
        changed(id);
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
            Person replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setPerson(replacement));
        }
        personRepository.delete(person);
        changed(id);
    }

    public Person findEntity(long id) {
        return personRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Человек с id " + id + " не найден"));
    }


    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.PERSON, id));
    }
}
