package repository;

import model.Event;

import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class EventRepository extends BaseRepository<Event> {

    public EventRepository() {
        super(Event.class, "SELECT e FROM Event e", Arrays.asList("name"), Arrays.asList("date", "minAge"));
    }
}
