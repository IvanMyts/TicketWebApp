package mapper;

import dto.VenueRequest;
import dto.VenueResponse;
import model.Venue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VenueMapper {

    public Venue toEntity(VenueRequest request) {
        Venue venue = new Venue();
        update(venue, request);
        return venue;
    }

    /** Полная замена полей (семантика PUT). id не трогается. */
    public void update(Venue target, VenueRequest request) {
        target.setName(request.getName());
        target.setCapacity(request.getCapacity());
        target.setType(request.getType());
    }

    public VenueResponse toResponse(Venue entity) {
        if (entity == null) {
            return null;
        }
        return new VenueResponse(
                entity.getId(),
                entity.getName(),
                entity.getCapacity(),
                entity.getType()
        );
    }
}
