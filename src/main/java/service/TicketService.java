package service;

import dto.request.PageQuery;
import dto.request.TicketRequest;
import dto.response.PageResponse;
import dto.response.TicketResponse;
import exception.ObjectNotFoundException;
import mapper.TicketMapper;
import model.Ticket;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.ArrayList;
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

    public PageResponse<TicketResponse> list(PageQuery query) {
        List<TicketResponse> content = new ArrayList<>();
        for (Ticket item : ticketRepository.findPage(query)) {
            content.add(TicketMapper.toResponse(item));
        }
        return PageResponse.of(content, query.getSize(), ticketRepository.count(query));
    }

    public TicketResponse get(long id) {
        return TicketMapper.toResponse(findEntity(id));
    }

    public TicketResponse create(TicketRequest request) {
        Ticket ticket = TicketMapper.toEntity(request);
        fillRelations(ticket, request);
        ticketRepository.save(ticket);
        return TicketMapper.toResponse(ticket);
    }

    public TicketResponse update(long id, TicketRequest request) {
        Ticket ticket = findEntity(id);
        TicketMapper.update(ticket, request);
        fillRelations(ticket, request);
        return TicketMapper.toResponse(ticket);
    }

    public void delete(long id) {
        ticketRepository.delete(findEntity(id));
    }

    public Ticket findEntity(long id) {
        Ticket found = ticketRepository.findById(id);
        if (found == null) {
            throw new ObjectNotFoundException("Билет с id " + id + " не найден");
        }
        return found;
    }

    private void fillRelations(Ticket ticket, TicketRequest request) {
        ticket.setPerson(null);
        if (request.getPersonId() != null) {
            ticket.setPerson(personService.findEntity(request.getPersonId()));
        }
        ticket.setEvent(null);
        if (request.getEventId() != null) {
            ticket.setEvent(eventService.findEntity(request.getEventId()));
        }
        ticket.setVenue(null);
        if (request.getVenueId() != null) {
            ticket.setVenue(venueService.findEntity(request.getVenueId()));
        }
    }
}
