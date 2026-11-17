package service;

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
import java.util.ArrayList;
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

    public PageResponse<PersonResponse> list(PageQuery query) {
        List<PersonResponse> content = new ArrayList<>();
        for (Person item : personRepository.findPage(query)) {
            content.add(PersonMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), personRepository.count(query));
    }

    public PersonResponse get(long id) {
        return PersonMapper.toResponse(findEntity(id));
    }

    public PersonResponse create(PersonRequest request) {
        Person person = PersonMapper.toEntity(request);
        setLocation(person, request);
        personRepository.save(person);
        return PersonMapper.toResponse(person);
    }

    public PersonResponse update(long id, PersonRequest request) {
        Person person = findEntity(id);
        PersonMapper.update(person, request);
        setLocation(person, request);
        return PersonMapper.toResponse(person);
    }

    public void delete(long id, Long replacementId) {
        Person person = findEntity(id);
        List<Ticket> tickets = ticketRepository.findReferencing("person", person);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Человек используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            Person replacement = findEntity(replacementId);
            for (Ticket ticket : tickets) {
                ticket.setPerson(replacement);
            }
        }
        personRepository.delete(person);
    }

    private void setLocation(Person person, PersonRequest request) {
        person.setLocation(null);
        if (request.getLocationId() != null) {
            person.setLocation(locationService.findEntity(request.getLocationId()));
        }
    }

    public Person findEntity(long id) {
        Person found = personRepository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Человек с id " + id + " не найден");
        }
        return found;
    }

}
