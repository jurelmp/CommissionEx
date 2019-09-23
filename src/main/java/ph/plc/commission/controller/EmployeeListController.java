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
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.gui.WindowManager;
import ph.plc.commission.model.Employee;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class EmployeeListController {

    @FXML
    public BorderPane employeeListPane;
    @FXML
    private TableView<Employee> tableViewEmployee;
    @FXML
    private TableColumn<Employee, String> colId;
    @FXML
    private TableColumn<Employee, String> colLastName;
    @FXML
    private TableColumn<Employee, String> colFirstName;
    @FXML
    private TableColumn<Employee, String> colMiddleInitial;
    @FXML
    private Button btnNewEmployee;
    @FXML
    private Button btnLocationList;
    @FXML
    private Button btnCompanyList;
    @FXML
    private Button btnEditEmployee;
    @FXML
    private Button btnViewCommissions;
    @FXML
    private Button btnAddCommission;
    @FXML
    private Button btnReports;

    @Inject
    private EmployeeService mEmployeeService;
    @Inject
    private WindowManager mWindowManager;

    private ObservableList<Employee> mEmployeeObservableList;
    private ContextMenu mContextMenu;
    private MenuItem mMenuItemEdit;
    private MenuItem mMenuItemAddCommission;
    private MenuItem mMenuItemCommissionList;
    private boolean isEditMode;
    private Employee mEmployee;

    @FXML
    private void initialize() {
        mEmployeeObservableList = FXCollections.observableArrayList();
        btnAddCommission.setVisible(false);
        contextMenuInit();
        isEditMode = false;
        setupBindings();
        setupListeners();
    }

    private void contextMenuInit() {
        mContextMenu = new ContextMenu();
        mMenuItemEdit = new MenuItem("Edit");
        mMenuItemCommissionList = new MenuItem("View Commissions");
        mMenuItemAddCommission = new MenuItem("Add Commission");
        mContextMenu.getItems().addAll(mMenuItemEdit, mMenuItemAddCommission, mMenuItemCommissionList);
    }

    private void setupBindings() {
        colId.setCellValueFactory(param -> param.getValue().codeProperty());
        colLastName.setCellValueFactory(param -> param.getValue().lastNameProperty());
        colFirstName.setCellValueFactory(param -> param.getValue().firstNameProperty());
        colMiddleInitial.setCellValueFactory(param -> param.getValue().middleInitialProperty());

        mEmployeeObservableList.addAll(mEmployeeService.getAll());
        tableViewEmployee.setItems(mEmployeeObservableList);

        TableFilter.forTableView(tableViewEmployee).lazy(true).apply();
    }

    private void setupListeners() {
        tableViewEmployee.setRowFactory(param -> {
            final TableRow<Employee> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(mContextMenu)
            );
            return row;
        });
        mMenuItemEdit.setOnAction(event -> {
            isEditMode = true;
            showEmployeeEditorDialog();
        });
        mMenuItemAddCommission.setOnAction(event -> showCommissionEditor());
        mMenuItemCommissionList.setOnAction(event -> showCommissionList());
        btnNewEmployee.setOnAction(event -> {
            isEditMode = false;
            showEmployeeEditorDialog();
        });
        btnEditEmployee.setOnAction(event -> {
            isEditMode = true;
            showEmployeeEditorDialog();
        });
        btnAddCommission.setOnAction(event -> showCommissionEditor());
        btnViewCommissions.setOnAction(event -> showCommissionList());
        btnLocationList.setOnAction(event -> mWindowManager.switchScene(WindowManager.SCENES.LOCATION_LIST_SCENE));
        btnCompanyList.setOnAction(event -> mWindowManager.switchScene(WindowManager.SCENES.COMPANY_LIST_SCENE));

        employeeListPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        newWindow.setOnCloseRequest(event -> {
                            Platform.exit();
                            System.exit(0);
                        });
                        Stage s = ((Stage) newWindow);
                        s.setMaximized(true);
                        s.getIcons().add(Helper.getImageIcon());
                    }
                });
            }
        });
        btnEditEmployee.disableProperty()
                .bind(Bindings.isEmpty(tableViewEmployee.getSelectionModel().getSelectedItems()));
        btnAddCommission.disableProperty()
                .bind(Bindings.isEmpty(tableViewEmployee.getSelectionModel().getSelectedItems()));
        btnViewCommissions.disableProperty()
                .bind(Bindings.isEmpty(tableViewEmployee.getSelectionModel().getSelectedItems()));
        btnReports.setOnAction(event -> showReportManager());
    }

    private void showReportManager() {
        mWindowManager.switchScene(WindowManager.SCENES.REPORT_MANAGER_SCENE);
    }

    private void showCommissionList() {
        mEmployee = tableViewEmployee.getSelectionModel().getSelectedItem();
        mWindowManager.switchScene(WindowManager.SCENES.COMMISSION_LIST_SCENE);
    }

    private void showCommissionEditor() {
        mEmployee = tableViewEmployee.getSelectionModel().getSelectedItem();
        mWindowManager.switchScene(WindowManager.SCENES.COMMISSION_EDITOR_SCENE);
    }

    private void showEmployeeEditorDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        TextField textFieldCode = new TextField();
        TextField textFieldFirstName = new TextField();
        TextField textFieldLastName = new TextField();
        TextField textFieldMiddleInitial = new TextField();
        textFieldMiddleInitial.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 1) {
                return null;
            } else {
                return change;
            }
        }));

        if (!isEditMode) {
            dialog.setTitle("New Employee");
            mEmployee = new Employee();
        } else {
            dialog.setTitle("Edit Employee");
            mEmployee = tableViewEmployee.getSelectionModel().getSelectedItem();
//            textFieldCode.setDisable(true);
            textFieldCode.setText(mEmployee.getCode());
            textFieldFirstName.setText(mEmployee.getFirstName());
            textFieldLastName.setText(mEmployee.getLastName());
            textFieldMiddleInitial.setText(mEmployee.getMiddleInitial());
        }

        int col = 0;
        int row = 0;
        gridPane.add(new Label("ID"), col, row);
        gridPane.add(textFieldCode, col + 1, row);

        gridPane.add(new Label("First Name"), col, ++row);
        gridPane.add(textFieldFirstName, col + 1, row);

        gridPane.add(new Label("Middle Initial"), col, ++row);
        gridPane.add(textFieldMiddleInitial, col + 1, row);

        gridPane.add(new Label("Last Name"), col, ++row);
        gridPane.add(textFieldLastName, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(textFieldCode::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType &&
                    (!textFieldCode.getText().isEmpty() &&
                            !textFieldFirstName.getText().isEmpty() &&
                            !textFieldLastName.getText().isEmpty())) {
                Employee e = new Employee();
                e.setCode(textFieldCode.getText());
                e.setLastName(textFieldLastName.getText());
                e.setFirstName(textFieldFirstName.getText());
                e.setMiddleInitial(textFieldMiddleInitial.getText());
                return e;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

        result.ifPresent(emp -> {
            mEmployee.setCode(emp.getCode());
            mEmployee.setFirstName(emp.getFirstName());
            mEmployee.setLastName(emp.getLastName());
            mEmployee.setMiddleInitial(emp.getMiddleInitial());
            Employee e = mEmployeeService.saveOrUpdate(mEmployee);
            if (!isEditMode) {
                mEmployeeObservableList.add(e);
            }
        });
    }

    public Employee getSelectedEmployee() {
        return mEmployee;
    }
}
