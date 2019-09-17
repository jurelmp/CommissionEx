package ph.plc.commission.database;

import ph.plc.commission.database.dao.IGenericDAO;
import ph.plc.commission.model.Commission;

import javax.inject.Inject;
import java.util.List;

public class CommissionService {

    private final IGenericDAO<Commission, Integer> mGenericDAO;

    @Inject
    public CommissionService(IGenericDAO<Commission, Integer> genericDAO) {
        mGenericDAO = genericDAO;
    }

    public List<Commission> getAll() {
        return mGenericDAO.getAll();
    }
}
