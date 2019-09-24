package ph.plc.commission.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.sf.jasperreports.engine.JRException;
import org.controlsfx.control.table.TableFilter;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.gui.WindowManager;
import ph.plc.commission.model.*;
import ph.plc.commission.report.ReportManager;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    @FXML
    private Button btnAddAdditional;
    @FXML
    private Button btnAddAllowance;
    @FXML
    private Button btnViewReport;

    @FXML
    private TextField allowanceDays;
    @FXML
    private TextField allowanceRate;
    @FXML
    private TextField additionalDescription;
    @FXML
    private TextField additionalAmount;

    @Inject
    private EmployeeService mEmployeeService;
    @Inject
    private EmployeeListController mEmployeeListController;
    @Inject
    private WindowManager mWindowManager;
    @Inject
    private ReportManager mReportManager;

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
        colTransactionDate.setSortType(TableColumn.SortType.DESCENDING);
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
        tableViewCommission.getSortOrder().add(colTransactionDate);

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
//                        s.initModality(Modality.APPLICATION_MODAL);
                        s.getIcons().add(Helper.getImageIcon());
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
        btnAddAllowance.setOnAction(event -> showAllowanceEditorDialog());
        btnAddAdditional.setOnAction(event -> showAdditionalEditorDialog());
        btnViewReport.setOnAction(event -> {
            try {
                showDriverCommissionSummaryReport();
            } catch (ClassNotFoundException | SQLException | JRException e) {
                e.printStackTrace();
            }
        });
        mEmployee.allowanceProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                allowanceDays.setText(String.valueOf(newValue.getDays()));
                allowanceRate.setText(String.valueOf(newValue.getRate()));
            }
        });
        mEmployee.additionalProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                additionalDescription.setText(newValue.getSubject());
                additionalAmount.setText(String.valueOf(newValue.getAmount()));
            }
        });
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

    private void showAllowanceEditorDialog() {
        Dialog<Allowance> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        Allowance allowance = mEmployee.getAllowance();

        if (mEmployee.getAllowance() == null) {
            allowance = new Allowance();
        }

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
                a.setEmployee(mEmployee);
                return a;
            }
            return null;
        });

        Optional<Allowance> result = dialog.showAndWait();

//        result.ifPresent(a -> {
//            mEmployee.setAllowance(a);
//        });
        if (result.isPresent()) {
            allowance.setDays(result.get().getDays());
            allowance.setRate(result.get().getRate());
            allowance.setOperationDate(result.get().getOperationDate());
            allowance.setEmployee(result.get().getEmployee());
            mEmployee.setAllowance(allowance);
            mEmployeeService.saveOrUpdate(mEmployee);
        }
    }

    private void showAdditionalEditorDialog() {
        Dialog<Additional> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        Additional additional = mEmployee.getAdditional();

        if (mEmployee.getAllowance() == null) {
            additional = new Additional();
        }

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
                a.setOperationDate(new Date());
                a.setEmployee(mEmployee);
                return a;
            }
            return null;
        });

        Optional<Additional> result = dialog.showAndWait();

        if (result.isPresent()) {
            additional.setSubject(result.get().getSubject());
            additional.setAmount(result.get().getAmount());
            additional.setOperationDate(result.get().getOperationDate());
            additional.setEmployee(result.get().getEmployee());
            mEmployee.setAdditional(additional);
            mEmployeeService.saveOrUpdate(mEmployee);
        }
    }

    private void showDriverCommissionSummaryReport() throws ClassNotFoundException, SQLException, JRException {
        Dialog<Pair<Date, Date>> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        DatePicker datePickerFrom = new DatePicker(LocalDate.now());
        DatePicker datePickerTo = new DatePicker(LocalDate.now());

        dialog.setTitle("Generate Report");

        int col = 0;
        int row = 0;
        gridPane.add(new Label("Period From"), col, row);
        gridPane.add(datePickerFrom, col + 1, row);

        gridPane.add(new Label("Period To"), col, ++row);
        gridPane.add(datePickerTo, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(datePickerFrom::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType) {
                return new Pair<>(Helper.toUtilDate(datePickerFrom.getValue()),
                        Helper.toUtilDate(datePickerTo.getValue()));
            }
            return null;
        });

        Optional<Pair<Date, Date>> result = dialog.showAndWait();
        if (result.isPresent()) {
            Map<String, Object> params = new HashMap<>();
            params.put(ReportManager.KEY.PERIOD_FROM.getKey(), result.get().getKey());
            params.put(ReportManager.KEY.PERIOD_TO.getKey(), result.get().getValue());
            params.put("EMPLOYEE_NAME", mEmployee.toString());
            params.put("EMPLOYEE_ID", mEmployee.getId());
            double allowanceDays = 0.0;
            double allowanceRate = 0.0;
            double additionalAmount = 0.0;
            String additionalText = "";

            if (mEmployee.getAllowance() != null) {
                Allowance all = mEmployee.getAllowance();
                allowanceDays = all.getDays();
                allowanceRate = all.getRate();
            }

            if (mEmployee.getAdditional() != null) {
                Additional add = mEmployee.getAdditional();
                additionalText = add.getSubject();
                additionalAmount = add.getAmount();
            }

            params.put("ALLOWANCE_DAYS", allowanceDays);
            params.put("ALLOWANCE_RATE", allowanceRate);
            params.put("ADDITIONAL_AMOUNT", additionalAmount);
            params.put("ADDITIONAL_TEXT", additionalText);
            mReportManager.generateReport(ReportManager.REPORT.DRIVER_COMMISSION_SUMMARY_REPORT, params);
        }
    }
}
