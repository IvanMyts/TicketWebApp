package service;

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
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class EventService {
    @Inject
    EventRepository repository;
    @Inject
    TicketRepository ticketRepository;

    public PageResponse<EventResponse> list(PageQuery query) {
        List<EventResponse> content = new ArrayList<>();
        for (Event item : repository.findPage(query)) {
            content.add(EventMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), repository.count(query));
    }

    public EventResponse get(int id) {
        return EventMapper.toResponse(findEntity(id));
    }

    public EventResponse create(EventRequest request) {
        Event entity = EventMapper.toEntity(request);
        repository.save(entity);
        return EventMapper.toResponse(entity);
    }

    public EventResponse update(int id, EventRequest request) {
        Event entity = findEntity(id);
        EventMapper.update(entity, request);
        return EventMapper.toResponse(entity);
    }

    public void delete(int id, Integer replacementId) {
        Event entity = findEntity(id);
        List<Ticket> tickets = ticketRepository.findReferencing("event", entity);
        if (!tickets.isEmpty()) {
            if (replacementId == null) {
                throw new ConflictException("Мероприятие используется (билетов: " + tickets.size() + "). Выберите замену.");
            }
            Event replacement = findEntity(replacementId);
            for (Ticket ticket : tickets) {
                ticket.setEvent(replacement);
            }
        }
        repository.delete(entity);
    }

    public Event findEntity(int id) {
        Event found = repository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Мероприятие с id " + id + " не найдено");
        }
        return found;
    }
}
