package service;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
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
import java.util.List;

/** Специальные операции над билетами из ТЗ. Реализованы в Java и JPQL, без функций и процедур БД. */
@ApplicationScoped
@Transactional
public class SpecialOperationService {
    @Inject
    TicketRepository ticketRepository;
    @Inject
    TicketService ticketService;
    @Inject
    PersonService personService;
    @Inject
    TicketMapper mapper;
    @Inject
    javax.enterprise.event.Event<EntityChangeEvent> changes;

    /** Количество билетов, у которых number меньше заданного. */
    public CountResponse countByNumberLessThan(double number) {
        return new CountResponse(ticketRepository.countByNumberLessThan(number));
    }

    /** Билеты, у которых name начинается с заданной подстроки. */
    public List<TicketResponse> findByNamePrefix(String prefix) {
        return ticketRepository.findByNameStartingWith(prefix).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Уникальные значения number по всем билетам. */
    public List<Double> distinctNumbers() {
        return ticketRepository.findDistinctNumbers();
    }

    /** Продать билет за указанную сумму указанному человеку. */
    public TicketResponse sell(long ticketId, SellTicketRequest request) {
        Ticket ticket = ticketService.findEntity(ticketId);
        ticket.setPerson(personService.findEntity(request.getPersonId()));
        ticket.setPrice(request.getPrice());
        changed(ticketId);
        return mapper.toResponse(ticket);
    }

    /** Скопировать билет: такой же, но с типом VIP и удвоенной ценой. */
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
        changed(copy.getId());
        return mapper.toResponse(copy);
    }

    private void changed(long id) {
        changes.fire(new EntityChangeEvent(EntityType.TICKET, id));
    }
}
