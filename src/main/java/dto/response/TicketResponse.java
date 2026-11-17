package dto.response;

import dto.CoordinatesDto;
import lombok.*;
import model.TicketType;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private long id;
    private String name;
    private CoordinatesDto coordinates;
    private ZonedDateTime creationDate;
    private PersonResponse person;
    private EventResponse event;
    private Double price;
    private TicketType type;
    private Integer discount;
    private Double number;
    private VenueResponse venue;
}
