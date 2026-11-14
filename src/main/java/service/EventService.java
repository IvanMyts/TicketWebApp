package service;

import dto.EventRequest;
import dto.EventResponse;
import dto.PageQuery;
import dto.PageResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;
import mapper.EventMapper;
import model.Event;
import model.Ticket;
import repository.EventRepository;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class EventService {
    @Inject
    EventRepository repository;
    @Inject
    TicketRepository ticketRepository;
    @Inject
    EventMapper mapper;

    public PageResponse<EventResponse> list(PageQuery query) {
        List<EventResponse> content = repository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getPage(), query.getSize(), repository.count(query));
    }

    public EventResponse get(int id) {
        return mapper.toResponse(findEntity(id));
    }

    public EventResponse create(EventRequest request) {
        return mapper.toResponse(createEntity(request));
    }

    public EventResponse update(int id, EventRequest request) {
        Event entity = findEntity(id);
        mapper.update(entity, request);
        return mapper.toResponse(entity);
    }

    /** Билеты с этим объектом перепривязываются к replacementId, затем объект удаляется. */
    public void delete(int id, Integer replacementId) {
        Event entity = findEntity(id);
        List<Ticket> tickets = ticketRepository.findByEvent(entity);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Мероприятие используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            if (replacementId == id) {
                throw new InvalidRequestException("Замена должна отличаться от удаляемого мероприятия");
            }
            Event replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setEvent(replacement));
        }
        repository.delete(entity);
    }

    public Event findEntity(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Мероприятие с id " + id + " не найдено"));
    }

    /** Связь при создании/изменении билета: существующий объект по id, новый из запроса или null. */
    public Event resolve(Integer id, EventRequest request) {
        if (id != null) {
            return findEntity(id);
        }
        return request != null ? createEntity(request) : null;
    }

    private Event createEntity(EventRequest request) {
        Event entity = mapper.toEntity(request);
        repository.save(entity);
        return entity;
    }
}
