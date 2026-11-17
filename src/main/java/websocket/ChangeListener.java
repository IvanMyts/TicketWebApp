package websocket;

import dto.response.EntityChangeEvent;
import dto.response.EntityChangeEvent.EntityType;
import model.Event;
import model.Location;
import model.Person;
import model.Ticket;
import model.Venue;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class ChangeListener {

    @PostPersist
    @PostUpdate
    @PostRemove
    public void changed(Object entity) {
        EntityChangeEvent event;
        if (entity instanceof Ticket t) {
            event = new EntityChangeEvent(EntityType.TICKET, t.getId());
        } else if (entity instanceof Person p) {
            event = new EntityChangeEvent(EntityType.PERSON, p.getId());
        } else if (entity instanceof Event e) {
            event = new EntityChangeEvent(EntityType.EVENT, e.getId().longValue());
        } else if (entity instanceof Venue v) {
            event = new EntityChangeEvent(EntityType.VENUE, v.getId().longValue());
        } else if (entity instanceof Location l) {
            event = new EntityChangeEvent(EntityType.LOCATION, l.getId());
        } else {
            return;
        }
        CDI.current().getBeanManager().getEvent().fire(event);
    }
}
