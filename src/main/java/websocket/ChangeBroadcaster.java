package websocket;

import dto.response.EntityChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@ApplicationScoped
public class ChangeBroadcaster {
    private static final Jsonb JSONB = JsonbBuilder.create();

    public void onChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) EntityChangeEvent event) {
        ChangesEndpoint.broadcast(JSONB.toJson(event));
    }
}
