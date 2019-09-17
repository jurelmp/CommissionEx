package ph.plc.commission.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class WindowManager {

    @Inject
    FXMLLoader mFXMLLoader;

    public enum SCENES {
        EMPLOYEE_LIST_SCENE("/views/EmployeeList.fxml"),
        COMMISSION_LIST_SCENE("/views/CommissionList.fxml"),
        COMPANY_LIST_SCENE("/views/CompanyList.fxml"),
        LOCATION_LIST_SCENE("/views/LocationList.fxml");

        private String sceneName;

        SCENES(String scenePath) {
            this.sceneName = scenePath;
        }

        public String getSceneName() {
            return sceneName;
        }
    }

    public void switchScene(SCENES scene) {
        mFXMLLoader.setRoot(null);
        mFXMLLoader.setController(null);
        mFXMLLoader.setLocation(getClass().getResource(scene.getSceneName()));
        Parent root = null;
        try {
            root = mFXMLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Wrong path: " + e.getMessage());
        }
        if (null == root) {
            throw new IllegalStateException("There was likely an error in the controller initialize() method.");
        }
        mFXMLLoader.getController();
        Stage stage = new Stage();
        stage.setTitle("Commission");
        stage.setScene(new Scene(root, 750, 550));
        stage.show();
    }
}
