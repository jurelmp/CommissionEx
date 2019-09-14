package ph.plc.commission.database;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;

public class JPAInitializer {
    @Inject
    JPAInitializer(PersistService service) {
        service.start();
    }
}
