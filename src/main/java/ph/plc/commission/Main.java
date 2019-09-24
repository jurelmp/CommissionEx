package ph.plc.commission;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.h2.tools.Server;
import ph.plc.commission.gui.GUI;
import ph.plc.commission.gui.GUIConfig;

import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new GUIConfig());
            }
        });

        try {
            Server.createTcpServer("-tcpAllowOthers").start();
            Class.forName("org.h2.Driver");
            DriverManager.getConnection("jdbc:h2:file:./data/commission;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=4");
            final Server webServer = Server.createWebServer();
            webServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GUI gui = injector.getInstance(GUI.class);

        try {
            gui.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
