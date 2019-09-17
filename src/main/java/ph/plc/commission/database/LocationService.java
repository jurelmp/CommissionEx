package ph.plc.commission.database;

import ph.plc.commission.database.dao.IGenericDAO;
import ph.plc.commission.model.Location;

import javax.inject.Inject;
import java.util.List;

public class LocationService {

    private final IGenericDAO<Location, Integer> mGenericDAO;

    @Inject
    public LocationService(IGenericDAO<Location, Integer> genericDAO) {
        mGenericDAO = genericDAO;
    }

    public List<Location> getAll() {
        return mGenericDAO.getAll();
    }

    public Location saveOrUpdate(Location l) {
        return mGenericDAO.saveOrUpdate(l);
    }
}
