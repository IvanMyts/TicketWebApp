package dto.request;

import dto.CoordinatesDto;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.*;
import model.TicketType;

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

    private Integer eventId;

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
}
