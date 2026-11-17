package mapper;

import dto.request.VenueRequest;
import dto.response.VenueResponse;
import model.Venue;

public class VenueMapper {

    public static Venue toEntity(VenueRequest request) {
        Venue venue = new Venue();
        update(venue, request);
        return venue;
    }

    public static void update(Venue target, VenueRequest request) {
        target.setName(request.getName());
        target.setCapacity(request.getCapacity());
        target.setType(request.getType());
    }

    public static VenueResponse toResponse(Venue entity) {
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
