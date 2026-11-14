package dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.*;
import model.VenueType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequest {
    @NotBlank(message = "Название площадки не может быть пустым")
    private String name;

    @Positive(message = "Вместимость должна быть больше 0")
    private Integer capacity;

    private VenueType type;
}
