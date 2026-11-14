package service;

import dto.PageQuery;
import dto.PageResponse;
import dto.TicketRequest;
import dto.TicketResponse;
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

    public PageResponse<TicketResponse> list(PageQuery query) {
        List<TicketResponse> content = ticketRepository.findPage(query).stream()
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, query.getPage(), query.getSize(), ticketRepository.count(query));
    }

    public TicketResponse get(long id) {
        return ticketRepository.findDetailedById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    public TicketResponse create(TicketRequest request) {
        Ticket ticket = mapper.toEntity(request);
        fillRelations(ticket, request);
        ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    public TicketResponse update(long id, TicketRequest request) {
        Ticket ticket = findEntity(id);
        mapper.update(ticket, request);
        fillRelations(ticket, request);
        return mapper.toResponse(ticket);
    }

    public void delete(long id) {
        ticketRepository.delete(findEntity(id));
    }

    public Ticket findEntity(long id) {
        return ticketRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void fillRelations(Ticket ticket, TicketRequest request) {
        ticket.setPerson(personService.resolve(request.getPersonId(), request.getPerson()));
        ticket.setEvent(eventService.resolve(request.getEventId(), request.getEvent()));
        ticket.setVenue(venueService.resolve(request.getVenueId(), request.getVenue()));
    }

    private ObjectNotFoundException notFound(long id) {
        return new ObjectNotFoundException("Билет с id " + id + " не найден");
    }
}
