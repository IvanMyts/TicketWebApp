package dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityChangeEvent {
    public static enum EntityType { TICKET, PERSON, EVENT, VENUE, LOCATION }

    private EntityType entityType;
    private Long id;
}
