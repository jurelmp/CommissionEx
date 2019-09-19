package ph.plc.commission.gui;

import com.gluonhq.ignite.guice.GuiceContext;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Collections;

public class GUI extends Application {

    private GuiceContext mGuiceContext;

    @Override
    public void start(Stage primaryStage) {
        mGuiceContext = new GuiceContext(this, () -> Collections.singletonList(new GUIConfig()));
        mGuiceContext.init();
        final WindowManager windowManager = mGuiceContext.getInstance(WindowManager.class);
        windowManager.switchScene(WindowManager.SCENES.COMMISSION_EDITOR_SCENE);
    }

    public void run(String[] args) {
        launch(args);
    }
}
