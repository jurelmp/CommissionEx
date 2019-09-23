package ph.plc.commission.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import ph.plc.commission.report.ReportManager;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ReportManagerController {

    @FXML
    private GridPane reportGridPane;
    @FXML
    private DatePicker datePickerFrom;
    @FXML
    private DatePicker datePickerTo;
    @FXML
    private Button btnHaulingIncome;
    @FXML
    private Button btnDeliveryCharges;
    @FXML
    private Button btnDriversCommission;

    @Inject
    private ReportManager mReportManager;

    @FXML
    private void initialize() {
        setupBindings();
        setupListeners();
    }

    private void setupListeners() {
        reportGridPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        Stage stage = ((Stage) newWindow);
                        stage.getIcons().add(new Image("/images/cost.png"));
                        stage.setTitle("Report Manager");
                        stage.setWidth(350.0);
                        stage.setHeight(235.0);
                        stage.setResizable(false);
                    }
                });
            }
        });
        btnHaulingIncome.setOnAction(event -> {
            try {
                showHaulingIncomeReport();
            } catch (ClassNotFoundException | SQLException | JRException e) {
                e.printStackTrace();
            }
        });
        btnDeliveryCharges.setOnAction(event -> {
            try {
                showDeliveryChargesReport();
            } catch (ClassNotFoundException | SQLException | JRException e) {
                e.printStackTrace();
            }
        });
        btnDriversCommission.setOnAction(event -> {
            try {
                showDriversCommissionSummaryReport();
            } catch (ClassNotFoundException | SQLException | JRException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupBindings() {
        datePickerTo.setValue(LocalDate.now());
        datePickerFrom.setValue(datePickerTo.getValue());
    }

    private void showDriversCommissionSummaryReport() throws ClassNotFoundException, SQLException, JRException {
        Map<String, Object> params = new HashMap<>();
        params.put(ReportManager.KEY.PERIOD_FROM.getKey(), Helper.toUtilDate(datePickerFrom.getValue()));
        params.put(ReportManager.KEY.PERIOD_TO.getKey(), Helper.toUtilDate(datePickerTo.getValue()));
        mReportManager.generateReport(ReportManager.REPORT.DRIVERS_COMMISSION_SUMMARY_REPORT, params);
    }

    private void showDeliveryChargesReport() throws ClassNotFoundException, SQLException, JRException {
        Map<String, Object> params = new HashMap<>();
        params.put(ReportManager.KEY.PERIOD_FROM.getKey(), Helper.toUtilDate(datePickerFrom.getValue()));
        params.put(ReportManager.KEY.PERIOD_TO.getKey(), Helper.toUtilDate(datePickerTo.getValue()));
        mReportManager.generateReport(ReportManager.REPORT.DELIVERY_CHARGES_REPORT, params);
    }

    private void showHaulingIncomeReport() throws ClassNotFoundException, SQLException, JRException {
        Map<String, Object> params = new HashMap<>();
        params.put(ReportManager.KEY.PERIOD_FROM.getKey(), Helper.toUtilDate(datePickerFrom.getValue()));
        params.put(ReportManager.KEY.PERIOD_TO.getKey(), Helper.toUtilDate(datePickerTo.getValue()));
        mReportManager.generateReport(ReportManager.REPORT.HAULING_INCOME_REPORT, params);
    }
}
