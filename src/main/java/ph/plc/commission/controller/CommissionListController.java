package ph.plc.commission.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;
import ph.plc.commission.database.CommissionService;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.gui.WindowManager;
import ph.plc.commission.model.Commission;
import ph.plc.commission.model.Company;
import ph.plc.commission.model.Employee;
import ph.plc.commission.model.Location;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class CommissionListController {

    private static final String FX_ALIGNMENT_CENTER_RIGHT = "-fx-alignment: CENTER-RIGHT;";

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
    @FXML
    private Button btnAddCommission;

    @Inject
    private CommissionService mCommissionService;
    @Inject
    private EmployeeService mEmployeeService;
    @Inject
    private EmployeeListController mEmployeeListController;
    @Inject
    private WindowManager mWindowManager;

    private ObservableList<Commission> mCommissionObservableList;
    private Employee mEmployee;

    // Context Menu
    private ContextMenu mContextMenu;
    private MenuItem mMenuItemRemove;

    @FXML
    private void initialize() {
        mCommissionObservableList = FXCollections.observableArrayList();
        mEmployee = mEmployeeListController.getSelectedEmployee();
        contextMenuInit();
        setupBindings();
        setupListeners();
    }

    private void contextMenuInit() {
        mContextMenu = new ContextMenu();
        mMenuItemRemove = new MenuItem("Remove");
        mContextMenu.getItems().add(mMenuItemRemove);
    }

    private void setupBindings() {
        colInvoice.setCellValueFactory(param -> param.getValue().invoiceProperty());
        colTransactionDate.setCellValueFactory(param -> param.getValue().transactionDateProperty());
        colTruckNo.setCellValueFactory(param -> param.getValue().truckNoProperty());
        colEmployee.setCellValueFactory(param -> param.getValue().employeeProperty());
        colCompany.setCellValueFactory(param -> param.getValue().companyProperty());
        colLocation.setCellValueFactory(param -> param.getValue().locationProperty());
        colVolume.setCellValueFactory(param -> param.getValue().volumeProperty().asObject());
        colVolume.setStyle(FX_ALIGNMENT_CENTER_RIGHT);
        colPumping.setCellValueFactory(param -> param.getValue().pumpingProperty().asObject());
        colPumping.setStyle(FX_ALIGNMENT_CENTER_RIGHT);
        colSuction.setCellValueFactory(param -> param.getValue().suctionProperty().asObject());
        colSuction.setStyle(FX_ALIGNMENT_CENTER_RIGHT);
        colRate.setCellValueFactory(param -> param.getValue().rateProperty().asObject());
        colRate.setStyle(FX_ALIGNMENT_CENTER_RIGHT);

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

        mCommissionObservableList.addAll(mEmployeeListController.getSelectedEmployee().getCommissions());
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
                        Stage s = ((Stage) newWindow);
                        s.initModality(Modality.APPLICATION_MODAL);
                    }
                });
            }
        });
        tableViewCommission.setRowFactory(param -> {
            final TableRow<Commission> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(mContextMenu)
            );
            return row;
        });
        mMenuItemRemove.setOnAction(event -> removeCommission());
        btnAddCommission.setOnAction(event -> showAddCommissionEditor());
    }

    private void showAddCommissionEditor() {
        mWindowManager.switchScene(WindowManager.SCENES.COMMISSION_EDITOR_SCENE);
    }

    private void removeCommission() {
        Commission selectedItem = tableViewCommission.getSelectionModel().getSelectedItem();
        if (mEmployee.getCommissions().remove(selectedItem)) {
            mEmployeeService.saveOrUpdate(mEmployee);
            mCommissionObservableList.remove(selectedItem);
        }
    }

    public void refreshList() {
        mCommissionObservableList.clear();
        mCommissionObservableList.addAll(mEmployee.getCommissions());
    }
}
