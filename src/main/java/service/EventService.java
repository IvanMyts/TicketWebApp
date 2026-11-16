package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
import dto.request.EventRequest;
import dto.response.EventResponse;
import dto.request.PageQuery;
import dto.response.PageResponse;
import exception.ConflictException;
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
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    public PageResponse<EventResponse> list(PageQuery query) {
        List<EventResponse> content = repository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getSize(), repository.count(query));
    }

    public EventResponse get(int id) {
        return mapper.toResponse(findEntity(id));
    }

    public EventResponse create(EventRequest request) {
        Event entity = mapper.toEntity(request);
        repository.save(entity);
        changed(entity.getId());
        return mapper.toResponse(entity);
    }

    public EventResponse update(int id, EventRequest request) {
        Event entity = findEntity(id);
        mapper.update(entity, request);
        changed(id);
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
            Event replacement = findEntity(replacementId);
            tickets.forEach(ticket -> ticket.setEvent(replacement));
        }
        repository.delete(entity);
        changed(id);
    }

    public Event findEntity(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Мероприятие с id " + id + " не найдено"));
    }

    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.EVENT, id));
    }
}
