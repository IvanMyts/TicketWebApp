package repository;

import model.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventRepository extends BaseRepository<Event> {

    public EventRepository() {
        super(Event.class, "SELECT e FROM Event e", "name");
    }
}
