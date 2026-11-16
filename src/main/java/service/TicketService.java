package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.TicketRequest;
import dto.response.TicketResponse;
import exception.ObjectNotFoundException;
import mapper.TicketMapper;
import model.Ticket;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class TicketService {
    @Inject
    TicketRepository ticketRepository;
    @Inject
    PersonService personService;
    @Inject
    EventService eventService;
    @Inject
    VenueService venueService;
    @Inject
    TicketMapper mapper;
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    public PageResponse<TicketResponse> list(PageQuery query) {
        List<TicketResponse> content = ticketRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getSize(), ticketRepository.count(query));
    }

    public TicketResponse get(long id) {
        return mapper.toResponse(findEntity(id));
    }

    public TicketResponse create(TicketRequest request) {
        Ticket ticket = mapper.toEntity(request);
        fillRelations(ticket, request);
        ticketRepository.save(ticket);
        changed(ticket.getId());
        return mapper.toResponse(ticket);
    }

    public TicketResponse update(long id, TicketRequest request) {
        Ticket ticket = findEntity(id);
        mapper.update(ticket, request);
        fillRelations(ticket, request);
        changed(id);
        return mapper.toResponse(ticket);
    }

    public void delete(long id) {
        ticketRepository.delete(findEntity(id));
        changed(id);
    }

    public Ticket findEntity(long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Билет с id " + id + " не найден"));
    }

    private void fillRelations(Ticket ticket, TicketRequest request) {
        ticket.setPerson(request.getPersonId() == null ? null : personService.findEntity(request.getPersonId()));
        ticket.setEvent(request.getEventId() == null ? null : eventService.findEntity(request.getEventId()));
        ticket.setVenue(request.getVenueId() == null ? null : venueService.findEntity(request.getVenueId()));
    }


    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.TICKET, id));
    }
}
