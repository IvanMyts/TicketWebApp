package mapper;

import dto.request.EventRequest;
import dto.response.EventResponse;
import model.Event;

public class EventMapper {

    public static Event toEntity(EventRequest request) {
        Event event = new Event();
        update(event, request);
        return event;
    }

    public static void update(Event target, EventRequest request) {
        target.setName(request.getName());
        target.setDate(request.getDate());
        target.setMinAge(request.getMinAge());
    }

    public static EventResponse toResponse(Event entity) {
        if (entity == null) {
            return null;
        }
        return new EventResponse(
                entity.getId(),
                entity.getName(),
                entity.getDate(),
                entity.getMinAge()
        );
    }
}
