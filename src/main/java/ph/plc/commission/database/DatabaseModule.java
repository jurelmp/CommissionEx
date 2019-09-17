package ph.plc.commission.database;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import ph.plc.commission.database.dao.GenericDAOImpl;
import ph.plc.commission.database.dao.IGenericDAO;
import ph.plc.commission.model.Commission;
import ph.plc.commission.model.Company;
import ph.plc.commission.model.Employee;
import ph.plc.commission.model.Location;

import java.util.Properties;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        install(createJpaPersistModule());
        bind(new TypeLiteral<IGenericDAO<Commission, Integer>>(){})
                .to(new TypeLiteral<GenericDAOImpl<Commission, Integer>>(){})
                .in(Scopes.SINGLETON);
        bind(new TypeLiteral<IGenericDAO<Employee, Integer>>(){})
                .to(new TypeLiteral<GenericDAOImpl<Employee, Integer>>(){})
                .in(Scopes.SINGLETON);
        bind(new TypeLiteral<IGenericDAO<Company, Integer>>(){})
                .to(new TypeLiteral<GenericDAOImpl<Company, Integer>>(){})
                .in(Scopes.SINGLETON);
        bind(new TypeLiteral<IGenericDAO<Location, Integer>>(){})
                .to(new TypeLiteral<GenericDAOImpl<Location, Integer>>(){})
                .in(Scopes.SINGLETON);
        bind(JPAInitializer.class).asEagerSingleton();
    }

    private JpaPersistModule createJpaPersistModule() {
        Properties properties = new Properties();

        properties.put("javax.persistence.jdbc.url","jdbc:log4jdbc:h2:file:./data/commission;DB_CLOSE_DELAY=-1");
        JpaPersistModule jpaPersistModule = new JpaPersistModule("commission-db");
        jpaPersistModule.properties(properties);
        return jpaPersistModule;
    }
}
