package ph.plc.commission.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ph.plc.commission.database.CommissionService;
import ph.plc.commission.database.CompanyService;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.database.LocationService;
import ph.plc.commission.model.Commission;
import ph.plc.commission.model.Company;
import ph.plc.commission.model.Employee;
import ph.plc.commission.model.Location;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class CommissionEditorController {

    private static final String FX_ALIGNMENT_CENTER_RIGHT = "-fx-alignment: CENTER-RIGHT;";

    // Root pane
    @FXML
    private BorderPane commissionEditorPane;

    // Form elements
    @FXML
    private DatePicker datePickerDate;
    @FXML
    private TextField textFieldInvoice;
    @FXML
    private TextField textFieldVolume;
    @FXML
    private TextField textFieldRate;
    @FXML
    private TextField textFieldPumping;
    @FXML
    private TextField textFieldSuction;
    @FXML
    private TextField textFieldTruckNo;
    @FXML
    private ComboBox<Location> comboBoxDestination;
    @FXML
    private ComboBox<Company> comboBoxCompany;
    @FXML
    private ComboBox<Employee> comboBoxEmployee;
    @FXML
    private Button btnSaveCommission;

    // Services
    @Inject
    private CommissionService mCommissionService;
    @Inject
    private CompanyService mCompanyService;
    @Inject
    private EmployeeService mEmployeeService;
    @Inject
    private LocationService mLocationService;

    @Inject
    private EmployeeListController mEmployeeListController;
    @Inject
    private CommissionListController mCommissionListController;
    private Employee mEmployeeSelected;

    private ObservableList<Company> mCompanyObservableList;
    private ObservableList<Employee> mEmployeeObservableList;
    private ObservableList<Location> mLocationObservableList;

    private ContextMenu mContextMenu;
    private MenuItem mMenuItemRemove;

    @FXML
    private void initialize() {
        datePickerDate.setValue(Helper.toLocalDate(new Date()));
        mCompanyObservableList = FXCollections.observableArrayList();
        mEmployeeObservableList = FXCollections.observableArrayList();
        mLocationObservableList = FXCollections.observableArrayList();
        mEmployeeSelected = mEmployeeListController.getSelectedEmployee();
        contextMenuInit();
        setupBindings();
        setupListeners();
        if (mEmployeeSelected != null) {
            comboBoxEmployee.getSelectionModel().select(mEmployeeSelected);
        }
    }

    private void setupBindings() {
        BooleanBinding txtDateValid = Bindings.createBooleanBinding(
                () -> datePickerDate.getValue() != null, datePickerDate.valueProperty());
        BooleanBinding txtVolumeValid = Bindings.createBooleanBinding(
                () -> textFieldVolume.getText() != null && !textFieldVolume.getText().isEmpty(), textFieldVolume.textProperty());
        BooleanBinding txtRateValid = Bindings.createBooleanBinding(
                () -> textFieldRate.getText() != null && !textFieldRate.getText().isEmpty(), textFieldRate.textProperty());
        BooleanBinding txtPumpingValid = Bindings.createBooleanBinding(
                () -> textFieldPumping.getText() != null && !textFieldPumping.getText().isEmpty(), textFieldPumping.textProperty());
        BooleanBinding txtSuctionValid = Bindings.createBooleanBinding(
                () -> textFieldSuction.getText() != null && !textFieldSuction.getText().isEmpty(), textFieldSuction.textProperty());
        BooleanBinding txtLocationValid = Bindings.createBooleanBinding(
                () -> comboBoxDestination.getValue() != null, comboBoxDestination.valueProperty());
        BooleanBinding txtCompanyValid = Bindings.createBooleanBinding(
                () -> comboBoxCompany.getValue() != null, comboBoxCompany.valueProperty());
        BooleanBinding txtEmployeeValid = Bindings.createBooleanBinding(
                () -> comboBoxEmployee.getValue() != null, comboBoxEmployee.valueProperty());

        btnSaveCommission.disableProperty().bind(
                txtDateValid.not()
                        .or(txtVolumeValid.not())
                        .or(txtRateValid.not())
                        .or(txtPumpingValid.not())
                        .or(txtSuctionValid.not())
                        .or(txtLocationValid.not())
                        .or(txtCompanyValid.not())
                        .or(txtEmployeeValid.not()));

        mCompanyObservableList.addAll(mCompanyService.getAll());
        mLocationObservableList.addAll(mLocationService.getAll());
        mEmployeeObservableList.addAll(mEmployeeService.getAll());
        comboBoxCompany.setItems(mCompanyObservableList);
        comboBoxDestination.setItems(mLocationObservableList);
        comboBoxEmployee.setItems(mEmployeeObservableList);
        textFieldRate.setTextFormatter(Helper.generateDoubleTextFormatter());
        textFieldVolume.setTextFormatter(Helper.generateIntegerTextFormatter());
        textFieldPumping.setTextFormatter(Helper.generateIntegerTextFormatter());
        textFieldSuction.setTextFormatter(Helper.generateIntegerTextFormatter());
    }

    private void setupListeners() {
        btnSaveCommission.setOnAction(event -> {
            saveCommission();
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        });
        commissionEditorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        Stage s = ((Stage) newWindow);
                        s.initModality(Modality.APPLICATION_MODAL);
                        s.setWidth(390.0);
                        s.setHeight(520.0);
                        s.setResizable(false);
                        s.getIcons().add(Helper.getImageIcon());
                        //prefHeight="478.0" prefWidth="388.0"
                    }
                });
            }
        });
    }

    private void contextMenuInit() {
        mContextMenu = new ContextMenu();
        mMenuItemRemove = new MenuItem("Delete");
    }

    private void saveCommission() {
        Commission commission = new Commission();
        commission.setTransactionDate(Helper.toUtilDate(datePickerDate.getValue()));
        commission.setInvoice(textFieldInvoice.getText());
        commission.setVolume(Integer.parseInt(textFieldVolume.getText()));
        commission.setRate(Double.parseDouble(textFieldRate.getText()));
        commission.setPumping(Integer.parseInt(textFieldPumping.getText()));
        commission.setSuction(Integer.parseInt(textFieldSuction.getText()));
        commission.setTruckNo(textFieldTruckNo.getText());
        commission.setLocation(comboBoxDestination.getValue());
        commission.setCompany(comboBoxCompany.getValue());
        commission.setEmployee(comboBoxEmployee.getValue());

        Employee employee = comboBoxEmployee.getValue();
        employee.getCommissions().add(commission);

        mEmployeeService.saveOrUpdate(employee);
        mCommissionListController.refreshList();
    }
}
