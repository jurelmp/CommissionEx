package ph.plc.commission.database;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

import java.util.Properties;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        install(createJpaPersistModule());
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
