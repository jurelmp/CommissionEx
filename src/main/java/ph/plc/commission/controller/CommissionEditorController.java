package ph.plc.commission.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
        contextMenuInit();
        setupBindings();
        setupListeners();
    }

    private void setupBindings() {
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
        btnSaveCommission.setOnAction(event -> saveCommission());
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
                a.setOperationDate(new Date());
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
                    (!textAreaAdditionalSubject.getText().isEmpty() &&
                            !textFieldAdditionalAmount.getText().isEmpty())) {
                Additional a = new Additional();
                a.setSubject(textAreaAdditionalSubject.getText());
                a.setAmount(Double.parseDouble(textFieldAdditionalAmount.getText()));
                a.setOperationDate(new Date());
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

        commission = mCommissionService.saveOrUpdate(commission);
        Employee employee = commission.getEmployee();
        employee.setAdditionals(new HashSet<>(mAdditionalObservableList));
        employee.setAllowances(new HashSet<>(mAllowanceObservableList));
        mEmployeeService.saveOrUpdate(employee);
    }
}
