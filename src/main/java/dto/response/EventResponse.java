package dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Integer id;
    private String name;
    private LocalDateTime date;
    private long minAge;
}
