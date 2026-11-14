package mapper;

import dto.LocationRequest;
import dto.LocationResponse;
import model.Location;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationMapper {

    public Location toEntity(LocationRequest request) {
        Location location = new Location();
        update(location, request);
        return location;
    }

    /** Полная замена полей (семантика PUT). id не трогается. */
    public void update(Location target, LocationRequest request) {
        target.setX(request.getX());
        target.setY(request.getY());
        target.setZ(request.getZ());
        target.setName(request.getName());
    }

    public LocationResponse toResponse(Location entity) {
        if (entity == null) {
            return null;
        }
        return new LocationResponse(
                entity.getId(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getName()
        );
    }
}
