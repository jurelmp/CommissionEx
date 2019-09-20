package ph.plc.commission.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ph.plc.commission.database.CommissionService;
import ph.plc.commission.database.CompanyService;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.database.LocationService;
import ph.plc.commission.model.*;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

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

    // Table elements for allowance
    @FXML
    private TableView<Allowance> tableViewAllowance;
    @FXML
    private TableColumn<Allowance, Double> colAllowanceDays;
    @FXML
    private TableColumn<Allowance, Double> colAllowanceRate;

    // Table elements for additional
    @FXML
    private TableView<Additional> tableViewAdditional;
    @FXML
    private TableColumn<Additional, String> colAdditionalDesc;
    @FXML
    private TableColumn<Additional, Double> colAdditionalAmount;

    @FXML
    private Button btnAddAllowance;
    @FXML
    private Button btnAddAdditional;
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
    private Employee mEmployeeSelected;

    private ObservableList<Company> mCompanyObservableList;
    private ObservableList<Employee> mEmployeeObservableList;
    private ObservableList<Location> mLocationObservableList;
    private ObservableList<Allowance> mAllowanceObservableList;
    private ObservableList<Additional> mAdditionalObservableList;

    private ContextMenu mContextMenu;
    private MenuItem mMenuItemRemove;

    @FXML
    private void initialize() {
        datePickerDate.setValue(Helper.toLocalDate(new Date()));
        mCompanyObservableList = FXCollections.observableArrayList();
        mEmployeeObservableList = FXCollections.observableArrayList();
        mLocationObservableList = FXCollections.observableArrayList();
        mAllowanceObservableList = FXCollections.observableArrayList();
        mAdditionalObservableList = FXCollections.observableArrayList();
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

        colAllowanceDays.setCellValueFactory(param -> param.getValue().daysProperty().asObject());
        colAllowanceRate.setCellValueFactory(param -> param.getValue().rateProperty().asObject());
        colAdditionalDesc.setCellValueFactory(param -> param.getValue().subjectProperty());
        colAdditionalAmount.setCellValueFactory(param -> param.getValue().amountProperty().asObject());
        colAllowanceRate.setStyle(FX_ALIGNMENT_CENTER_RIGHT);
        colAdditionalAmount.setStyle(FX_ALIGNMENT_CENTER_RIGHT);

        mCompanyObservableList.addAll(mCompanyService.getAll());
        mLocationObservableList.addAll(mLocationService.getAll());
        mEmployeeObservableList.addAll(mEmployeeService.getAll());
        comboBoxCompany.setItems(mCompanyObservableList);
        comboBoxDestination.setItems(mLocationObservableList);
        comboBoxEmployee.setItems(mEmployeeObservableList);
        tableViewAllowance.setItems(mAllowanceObservableList);
        tableViewAdditional.setItems(mAdditionalObservableList);
        textFieldRate.setTextFormatter(Helper.generateDoubleTextFormatter());
        textFieldVolume.setTextFormatter(Helper.generateIntegerTextFormatter());
        textFieldPumping.setTextFormatter(Helper.generateIntegerTextFormatter());
        textFieldSuction.setTextFormatter(Helper.generateIntegerTextFormatter());
    }

    private void setupListeners() {
        btnAddAllowance.setOnAction(event -> showAllowanceEditorDialog());
        btnAddAdditional.setOnAction(event -> showAdditionalEditorDialog());
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
                        ((Stage) newWindow).initModality(Modality.APPLICATION_MODAL);
                    }
                });
            }
        });
    }

    private void contextMenuInit() {
        mContextMenu = new ContextMenu();
        mMenuItemRemove = new MenuItem("Delete");
    }

    private void showAllowanceEditorDialog() {
        Dialog<Allowance> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        TextField textFieldAllowanceDays = new TextField();
        textFieldAllowanceDays.setTextFormatter(Helper.generateDoubleTextFormatter());
        TextField textFieldAllowanceRate = new TextField();
        textFieldAllowanceRate.setTextFormatter(Helper.generateDoubleTextFormatter());

        dialog.setTitle("Allowance");

        int col = 0;
        int row = 0;
        gridPane.add(new Label("Days"), col, row);
        gridPane.add(textFieldAllowanceDays, col + 1, row);

        gridPane.add(new Label("Rate"), col, ++row);
        gridPane.add(textFieldAllowanceRate, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(textFieldAllowanceDays::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType &&
                    (!textFieldAllowanceDays.getText().isEmpty() &&
                            !textFieldAllowanceRate.getText().isEmpty())) {
                Allowance a = new Allowance();
                a.setDays(Double.parseDouble(textFieldAllowanceDays.getText()));
                a.setRate(Double.parseDouble(textFieldAllowanceRate.getText()));
                a.setOperationDate(Helper.toUtilDate(datePickerDate.getValue()));
                return a;
            }
            return null;
        });

        Optional<Allowance> result = dialog.showAndWait();

        result.ifPresent(allowance -> mAllowanceObservableList.add(allowance));
    }

    private void showAdditionalEditorDialog() {
        Dialog<Additional> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        TextArea textAreaAdditionalSubject = new TextArea();
        textAreaAdditionalSubject.setPrefRowCount(2);
        TextField textFieldAdditionalAmount = new TextField();
        textFieldAdditionalAmount.setTextFormatter(Helper.generateDoubleTextFormatter());

        dialog.setTitle("Additional");

        int col = 0;
        int row = 0;
        gridPane.add(new Label("Description"), col, row);
        gridPane.add(textAreaAdditionalSubject, col + 1, row);

        gridPane.add(new Label("Amount"), col, ++row);
        gridPane.add(textFieldAdditionalAmount, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(textAreaAdditionalSubject::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType &&
                    !textFieldAdditionalAmount.getText().isEmpty()) {
                Additional a = new Additional();
                a.setSubject(textAreaAdditionalSubject.getText());
                a.setAmount(Double.parseDouble(textFieldAdditionalAmount.getText()));
                a.setOperationDate(Helper.toUtilDate(datePickerDate.getValue()));
                return a;
            }
            return null;
        });

        Optional<Additional> result = dialog.showAndWait();

        result.ifPresent(additional -> mAdditionalObservableList.add(additional));
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

        if (mAdditionalObservableList.size() > 0) {
            mAdditionalObservableList.forEach(additional -> additional.setEmployee(employee));
            employee.setAdditionals(new HashSet<>(mAdditionalObservableList));
        }

        if (mAllowanceObservableList.size() > 0) {
            mAllowanceObservableList.forEach(allowance -> allowance.setEmployee(employee));
            employee.setAllowances(new HashSet<>(mAllowanceObservableList));
        }

        mEmployeeService.saveOrUpdate(employee);
    }
}
