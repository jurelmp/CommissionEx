package ph.plc.commission.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;
import ph.plc.commission.database.CommissionService;
import ph.plc.commission.model.Commission;
import ph.plc.commission.model.Company;
import ph.plc.commission.model.Employee;
import ph.plc.commission.model.Location;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class CommissionListController {

    @FXML
    private BorderPane commissionListPane;
    @FXML
    private TableView<Commission> tableViewCommission;
    @FXML
    private TableColumn<Commission, String> colInvoice;
    @FXML
    private TableColumn<Commission, Date> colTransactionDate;
    @FXML
    private TableColumn<Commission, String> colTruckNo;
    @FXML
    private TableColumn<Commission, Employee> colEmployee;
    @FXML
    private TableColumn<Commission, Company> colCompany;
    @FXML
    private TableColumn<Commission, Location> colLocation;
    @FXML
    private TableColumn<Commission, Integer> colVolume;
    @FXML
    private TableColumn<Commission, Integer> colPumping;
    @FXML
    private TableColumn<Commission, Integer> colSuction;
    @FXML
    private TableColumn<Commission, Double> colRate;

    @Inject
    private CommissionService mCommissionService;

    private ObservableList<Commission> mCommissionObservableList;

    @FXML
    private void initialize() {
        mCommissionObservableList = FXCollections.observableArrayList();
        setupBindings();
        setupListeners();
    }

    private void setupBindings() {
        colInvoice.setCellValueFactory(param -> param.getValue().invoiceProperty());
        colTransactionDate.setCellValueFactory(param -> param.getValue().transactionDateProperty());
        colTruckNo.setCellValueFactory(param -> param.getValue().truckNoProperty());
        colEmployee.setCellValueFactory(param -> param.getValue().employeeProperty());
        colCompany.setCellValueFactory(param -> param.getValue().companyProperty());
        colLocation.setCellValueFactory(param -> param.getValue().locationProperty());
        colVolume.setCellValueFactory(param -> param.getValue().volumeProperty().asObject());
        colPumping.setCellValueFactory(param -> param.getValue().pumpingProperty().asObject());
        colSuction.setCellValueFactory(param -> param.getValue().suctionProperty().asObject());
        colRate.setCellValueFactory(param -> param.getValue().rateProperty().asObject());

        colEmployee.setCellFactory(param -> new TableCell<Commission, Employee>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        colCompany.setCellFactory(param -> new TableCell<Commission, Company>() {
            @Override
            protected void updateItem(Company item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        colLocation.setCellFactory(param -> new TableCell<Commission, Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        mCommissionObservableList.addAll(mCommissionService.getAll());
        tableViewCommission.setItems(mCommissionObservableList);

        TableFilter.forTableView(tableViewCommission).lazy(true).apply();
    }

    private void setupListeners() {
        commissionListPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        newWindow.setOnCloseRequest(event -> {
                            Platform.exit();
                            System.exit(0);
                        });
                        ((Stage) newWindow).setMaximized(true);
                    }
                });
            }
        });
    }
}
