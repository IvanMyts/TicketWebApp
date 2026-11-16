package mapper;

import dto.request.EventRequest;
import dto.response.EventResponse;
import model.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventMapper {

    public Event toEntity(EventRequest request) {
        Event event = new Event();
        update(event, request);
        return event;
    }

    /** Полная замена полей (семантика PUT). id не трогается. */
    public void update(Event target, EventRequest request) {
        target.setName(request.getName());
        target.setDate(request.getDate());
        target.setMinAge(request.getMinAge());
    }

    public EventResponse toResponse(Event entity) {
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
