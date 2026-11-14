package dto;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.*;
import model.TicketType;

/**
 * Тело запроса на создание/обновление билета.
 * id и creationDate не передаются — их генерирует БД/сервер.
 * Для каждой связи можно указать либо ID существующего объекта, либо новый объект.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    @NotBlank(message = "Название билета не может быть пустым")
    @Size(max = 100, message = "Название билета не длиннее 100 символов")
    private String name;

    @NotNull(message = "Координаты обязательны")
    @Valid
    private CoordinatesDto coordinates;

    private Long personId;
    @Valid
    private PersonRequest person;

    private Integer eventId;
    @Valid
    private EventRequest event;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть больше 0")
    private Double price;

    private TicketType type;

    @NotNull(message = "Скидка обязательна")
    @Positive(message = "Скидка должна быть больше 0")
    @Max(value = 100, message = "Скидка не может быть больше 100")
    private Integer discount;

    @Positive(message = "Номер должен быть больше 0")
    private Double number;

    private Integer venueId;
    @Valid
    private VenueRequest venue;

    @JsonbTransient
    @AssertTrue(message = "Укажите либо существующего человека, либо нового, но не обоих сразу")
    public boolean isPersonUnambiguous() {
        return personId == null || person == null;
    }

    @JsonbTransient
    @AssertTrue(message = "Укажите либо существующее мероприятие, либо новое, но не оба сразу")
    public boolean isEventUnambiguous() {
        return eventId == null || event == null;
    }

    @JsonbTransient
    @AssertTrue(message = "Укажите либо существующую площадку, либо новую, но не обе сразу")
    public boolean isVenueUnambiguous() {
        return venueId == null || venue == null;
    }
}
