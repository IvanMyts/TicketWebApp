package dto.response;

import lombok.*;
import model.VenueType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponse {
    private Integer id;
    private String name;
    private Integer capacity;
    private VenueType type;
}
