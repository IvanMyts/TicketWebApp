package repository;

import exception.InvalidRequestException;
import model.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventRepository extends BaseRepository<Event> {

    public EventRepository() {
        super("Event", "SELECT e FROM Event e");
    }

    @Override
    protected String textColumn(String column) {
        switch (column) {
            case "name":
                return "e.name";
            default:
                throw new InvalidRequestException("Недопустимая колонка: " + column);
        }
    }
}
