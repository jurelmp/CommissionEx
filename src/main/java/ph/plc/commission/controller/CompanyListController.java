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
import ph.plc.commission.database.CompanyService;
import ph.plc.commission.model.Company;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class CompanyListController {

    @FXML
    private BorderPane companyListPane;
    @FXML
    private TableView<Company> tableViewCompany;
    @FXML
    private TableColumn<Company, String> colName;
    @FXML
    private TableColumn<Company, String> colDescription;
    @FXML
    private Button btnNewCompany;

    @Inject
    private CompanyService mCompanyService;

    private ObservableList<Company> mCompanyObservableList;
    private ContextMenu mContextMenu;
    private MenuItem mMenuItemEdit;
    private boolean isEditMode;
    private Company mCompany;

    @FXML
    private void initialize() {
        mCompanyObservableList = FXCollections.observableArrayList();
        mContextMenu = new ContextMenu();
        mMenuItemEdit = new MenuItem("Edit");
        mContextMenu.getItems().add(mMenuItemEdit);
        isEditMode = false;
        setupBindings();
        setupListeners();
    }

    private void setupBindings() {
        colName.setCellValueFactory(param -> param.getValue().nameProperty());
        colDescription.setCellValueFactory(param -> param.getValue().descriptionProperty());

        mCompanyObservableList.addAll(mCompanyService.getAll());
        tableViewCompany.setItems(mCompanyObservableList);

        TableFilter.forTableView(tableViewCompany).lazy(true).apply();
    }

    private void setupListeners() {
        tableViewCompany.setRowFactory(param -> {
            final TableRow<Company> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(mContextMenu)
            );
            return row;
        });
        mMenuItemEdit.setOnAction(event -> {
            isEditMode = true;
            showCompanyEditorDialog();
        });
        btnNewCompany.setOnAction(event -> {
            isEditMode = false;
            showCompanyEditorDialog();
        });
        companyListPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        Stage s = ((Stage) newWindow);
                        s.getIcons().add(Helper.getImageIcon());
                    }
                });
            }
        });
    }

    private void showCompanyEditorDialog() {
        Dialog<Company> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        TextField textFieldName = new TextField();
        TextArea textAreaDescription = new TextArea();
        textAreaDescription.setPrefRowCount(5);

        if (!isEditMode) {
            dialog.setTitle("New Company");
            mCompany = new Company();
        } else {
            dialog.setTitle("Edit Company");
            mCompany = tableViewCompany.getSelectionModel().getSelectedItem();
            textFieldName.setText(mCompany.getName());
            textAreaDescription.setText(mCompany.getDescription());
        }

        int col = 0;
        int row = 0;
        gridPane.add(new Label("Name"), col, row);
        gridPane.add(textFieldName, col + 1, row);

        gridPane.add(new Label("Description (optional)"), col, ++row);
        gridPane.add(textAreaDescription, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(textFieldName::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType &&
                    !textFieldName.getText().isEmpty()) {
                Company c = new Company();
                c.setName(textFieldName.getText());
                c.setDescription(textAreaDescription.getText());
                return c;
            }
            return null;
        });

        Optional<Company> result = dialog.showAndWait();

        result.ifPresent(c -> {
            mCompany.setName(c.getName());
            mCompany.setDescription(c.getDescription());
            Company company = mCompanyService.saveOrUpdate(mCompany);
            if (!isEditMode) {
                mCompanyObservableList.add(company);
            }
        });
    }
}
