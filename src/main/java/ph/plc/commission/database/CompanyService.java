package ph.plc.commission.database;

import ph.plc.commission.database.dao.IGenericDAO;
import ph.plc.commission.model.Company;

import javax.inject.Inject;
import java.util.List;

public class CompanyService {

    private final IGenericDAO<Company, Integer> mGenericDAO;

    @Inject
    public CompanyService(IGenericDAO<Company, Integer> genericDAO) {
        mGenericDAO = genericDAO;
    }

    public List<Company> getAll() {
        return mGenericDAO.getAll();
    }

    public Company saveOrUpdate(Company c) {
        return mGenericDAO.saveOrUpdate(c);
    }

    public void remove(Company c) {
        mGenericDAO.remove(c);
    }
}
