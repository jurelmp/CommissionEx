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
import org.controlsfx.control.table.TableFilter;
import ph.plc.commission.database.EmployeeService;
import ph.plc.commission.model.Employee;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class EmployeeListController {

    @FXML
    private BorderPane employeeListPane;
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

    @Inject
    private EmployeeService mEmployeeService;

    private ObservableList<Employee> mEmployeeObservableList;
    private ContextMenu mContextMenu;
    private MenuItem mMenuItemEdit;
    private boolean isEditMode;
    private Employee mEmployee;

    @FXML
    private void initialize() {
        mEmployeeObservableList = FXCollections.observableArrayList();
        mContextMenu = new ContextMenu();
        mMenuItemEdit = new MenuItem("Edit");
        mContextMenu.getItems().add(mMenuItemEdit);
        isEditMode = false;
        setupBindings();
        setupListeners();
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
        btnNewEmployee.setOnAction(event -> {
            isEditMode = false;
            showEmployeeEditorDialog();
        });
    }

    private void showEmployeeEditorDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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
}
