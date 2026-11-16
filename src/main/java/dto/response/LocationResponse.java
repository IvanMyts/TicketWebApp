package dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private Long x;
    private Integer y;
    private Double z;
    private String name;
}
