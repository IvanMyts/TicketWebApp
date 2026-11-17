package repository;

import model.Location;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationRepository extends BaseRepository<Location> {

    public LocationRepository() {
        super(Location.class, "SELECT e FROM Location e", "name");
    }
}
