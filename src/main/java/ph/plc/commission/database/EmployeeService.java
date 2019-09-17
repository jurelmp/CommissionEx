package ph.plc.commission.database;

import ph.plc.commission.database.dao.IGenericDAO;
import ph.plc.commission.model.Employee;

import javax.inject.Inject;
import java.util.List;

public class EmployeeService {

    private final IGenericDAO<Employee, Integer> mGenericDAO;

    @Inject
    public EmployeeService(IGenericDAO<Employee, Integer> genericDAO) {
        mGenericDAO = genericDAO;
    }

    public List<Employee> getAll() {
        return mGenericDAO.getAll();
    }

    public Employee saveOrUpdate(Employee e) {
        return mGenericDAO.saveOrUpdate(e);
    }

    public void remove(Employee e) {
        mGenericDAO.remove(e);
    }
}
