package dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    @NotNull(message = "Координата X локации обязательна")
    private Long x;

    @NotNull(message = "Координата Y локации обязательна")
    private Integer y;

    @NotNull(message = "Координата Z локации обязательна")
    private Double z;

    @NotBlank(message = "Название локации не может быть пустым")
    private String name;
}
