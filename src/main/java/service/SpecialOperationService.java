package service;

import dto.response.CountResponse;
import dto.request.SellTicketRequest;
import dto.response.TicketResponse;
import mapper.TicketMapper;
import model.Coordinates;
import model.Ticket;
import model.TicketType;
import repository.TicketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class SpecialOperationService {
    @Inject
    TicketRepository ticketRepository;
    @Inject
    TicketService ticketService;
    @Inject
    PersonService personService;

    public CountResponse countByNumberLessThan(double number) {
        return new CountResponse(ticketRepository.countByNumberLessThan(number));
    }

    public List<TicketResponse> findByNamePrefix(String prefix) {
        List<TicketResponse> result = new ArrayList<>();
        for (Ticket ticket : ticketRepository.findByNameStartingWith(prefix)) {
            result.add(TicketMapper.toResponse(ticket));
        }
        return result;
    }

    public List<Double> distinctNumbers() {
        return ticketRepository.findDistinctNumbers();
    }

    public TicketResponse sell(long ticketId, SellTicketRequest request) {
        Ticket ticket = ticketService.findEntity(ticketId);
        ticket.setPerson(personService.findEntity(request.getPersonId()));
        ticket.setPrice(request.getPrice());
        return TicketMapper.toResponse(ticket);
    }

    public TicketResponse copyAsVip(long ticketId) {
        Ticket source = ticketService.findEntity(ticketId);

        Ticket copy = new Ticket();
        copy.setName(source.getName());
        copy.setCoordinates(new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()));
        copy.setPerson(source.getPerson());
        copy.setEvent(source.getEvent());
        copy.setPrice(source.getPrice() * 2);
        copy.setType(TicketType.VIP);
        copy.setDiscount(source.getDiscount());
        copy.setNumber(source.getNumber());
        copy.setVenue(source.getVenue());

        ticketRepository.save(copy);
        return TicketMapper.toResponse(copy);
    }
}
