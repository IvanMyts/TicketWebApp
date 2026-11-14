package repository;

import exception.InvalidRequestException;
import model.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventRepository extends BaseRepository<Event, Integer> {

    public EventRepository() {
        super(Event.class, "e", "SELECT e FROM Event e");
    }

    @Override
    protected String textColumn(String column) {
        return switch (column) {
            case "name" -> "e.name";
            default -> throw new InvalidRequestException("Недопустимая колонка: " + column);
        };
    }
}
