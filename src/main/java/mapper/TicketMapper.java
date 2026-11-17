package mapper;

import dto.CoordinatesDto;
import dto.request.TicketRequest;
import dto.response.TicketResponse;
import model.Coordinates;
import model.Ticket;

public class TicketMapper {

    public static Ticket toEntity(TicketRequest request) {
        Ticket ticket = new Ticket();
        update(ticket, request);
        return ticket;
    }

    public static void update(Ticket target, TicketRequest request) {
        target.setName(request.getName());
        target.setCoordinates(new Coordinates(request.getCoordinates().getX(), request.getCoordinates().getY()));
        target.setPrice(request.getPrice());
        target.setType(request.getType());
        target.setDiscount(request.getDiscount());
        target.setNumber(request.getNumber());
    }

    public static TicketResponse toResponse(Ticket entity) {
        if (entity == null) {
            return null;
        }
        return new TicketResponse(
                entity.getId(),
                entity.getName(),
                new CoordinatesDto(entity.getCoordinates().getX(), entity.getCoordinates().getY()),
                entity.getCreationDate(),
                PersonMapper.toResponse(entity.getPerson()),
                EventMapper.toResponse(entity.getEvent()),
                entity.getPrice(),
                entity.getType(),
                entity.getDiscount(),
                entity.getNumber(),
                VenueMapper.toResponse(entity.getVenue())
        );
    }
}
