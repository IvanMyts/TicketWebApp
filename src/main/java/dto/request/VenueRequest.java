package dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.*;
import model.VenueType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequest {
    @NotBlank(message = "Название площадки не может быть пустым")
    @Size(max = 100, message = "Название площадки не длиннее 100 символов")
    private String name;

    @Positive(message = "Вместимость должна быть больше 0")
    private Integer capacity;

    private VenueType type;
}
