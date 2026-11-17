package mapper;

import dto.request.LocationRequest;
import dto.response.LocationResponse;
import model.Location;

public class LocationMapper {

    public static Location toEntity(LocationRequest request) {
        Location location = new Location();
        update(location, request);
        return location;
    }

    public static void update(Location target, LocationRequest request) {
        target.setX(request.getX());
        target.setY(request.getY());
        target.setZ(request.getZ());
        target.setName(request.getName());
    }

    public static LocationResponse toResponse(Location entity) {
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
