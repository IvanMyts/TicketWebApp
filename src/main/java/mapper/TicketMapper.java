package mapper;

import dto.CoordinatesDto;
import dto.request.TicketRequest;
import dto.response.TicketResponse;
import model.Coordinates;
import model.Ticket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Связи с Person, Event и Venue здесь не устанавливаются: по id их нужно найти в БД,
 * а новые объекты из запроса — создать. Это делает сервис.
 * id и creationDate генерируются БД/сущностью и из запроса не берутся.
 */
@ApplicationScoped
public class TicketMapper {

    @Inject
    private PersonMapper personMapper;

    @Inject
    private EventMapper eventMapper;

    @Inject
    private VenueMapper venueMapper;

    public Ticket toEntity(TicketRequest request) {
        Ticket ticket = new Ticket();
        update(ticket, request);
        return ticket;
    }

    /** Полная замена простых полей (семантика PUT). id, creationDate и связи не трогаются. */
    public void update(Ticket target, TicketRequest request) {
        target.setName(request.getName());
        target.setCoordinates(new Coordinates(request.getCoordinates().getX(), request.getCoordinates().getY()));
        target.setPrice(request.getPrice());
        target.setType(request.getType());
        target.setDiscount(request.getDiscount());
        target.setNumber(request.getNumber());
    }

    public TicketResponse toResponse(Ticket entity) {
        return new TicketResponse(
                entity.getId(),
                entity.getName(),
                new CoordinatesDto(entity.getCoordinates().getX(), entity.getCoordinates().getY()),
                entity.getCreationDate(),
                personMapper.toResponse(entity.getPerson()),
                eventMapper.toResponse(entity.getEvent()),
                entity.getPrice(),
                entity.getType(),
                entity.getDiscount(),
                entity.getNumber(),
                venueMapper.toResponse(entity.getVenue())
        );
    }
}
