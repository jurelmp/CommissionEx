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
import ph.plc.commission.database.LocationService;
import ph.plc.commission.model.Location;
import ph.plc.commission.util.Helper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class LocationListController {

    @FXML
    private BorderPane locationListPane;
    @FXML
    private TableView<Location> tableViewLocation;
    @FXML
    private TableColumn<Location, String> colArea;
    @FXML
    private TableColumn<Location, String> colLocation;
    @FXML
    private Button btnNewLocation;

    @Inject
    LocationService mLocationService;

    private ObservableList<Location> mLocationObservableList;
    private ContextMenu mContextMenu;
    private MenuItem mMenuItemEdit;
    private boolean isEditMode;
    private Location mLocation;

    @FXML
    private void initialize() {
        mLocationObservableList = FXCollections.observableArrayList();
        mContextMenu = new ContextMenu();
        mMenuItemEdit = new MenuItem("Edit");
        mContextMenu.getItems().add(mMenuItemEdit);
        isEditMode = false;
        setupBindings();
        setupListeners();
    }

    private void setupBindings() {
        colArea.setCellValueFactory(param -> param.getValue().areaProperty());
        colLocation.setCellValueFactory(param -> param.getValue().locationProperty());

        mLocationObservableList.addAll(mLocationService.getAll());
        tableViewLocation.setItems(mLocationObservableList);

        TableFilter.forTableView(tableViewLocation).lazy(true).apply();
    }

    private void setupListeners() {
        tableViewLocation.setRowFactory(param -> {
            final TableRow<Location> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(mContextMenu)
            );
            return row;
        });
        mMenuItemEdit.setOnAction(event -> {
            isEditMode = true;
            showLocationEditorDialog();
        });
        btnNewLocation.setOnAction(event -> {
            isEditMode = false;
            showLocationEditorDialog();
        });
        locationListPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
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

    private void showLocationEditorDialog() {
        Dialog<Location> dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(Helper.getImageIcon());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 15, 10, 15));

        TextField textFieldArea = new TextField();
        TextField textFieldLocation = new TextField();

        if (!isEditMode) {
            dialog.setTitle("New Location");
            mLocation = new Location();
        } else {
            dialog.setTitle("Edit Location");
            mLocation = tableViewLocation.getSelectionModel().getSelectedItem();
            textFieldArea.setText(mLocation.getArea());
            textFieldLocation.setText(mLocation.getLocation());
        }

        int col = 0;
        int row = 0;
        gridPane.add(new Label("Area"), col, row);
        gridPane.add(textFieldArea, col + 1, row);

        gridPane.add(new Label("Location"), col, ++row);
        gridPane.add(textFieldLocation, col + 1, row);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(textFieldArea::requestFocus);

        dialog.setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType &&
                    (!textFieldArea.getText().isEmpty() &&
                            !textFieldLocation.getText().isEmpty())) {
                Location l = new Location();
                l.setArea(textFieldArea.getText());
                l.setLocation(textFieldLocation.getText());
                return l;
            }
            return null;
        });

        Optional<Location> result = dialog.showAndWait();

        result.ifPresent(loc -> {
            mLocation.setArea(loc.getArea());
            mLocation.setLocation(loc.getLocation());
            Location l = mLocationService.saveOrUpdate(mLocation);
            if (!isEditMode) {
                mLocationObservableList.add(l);
            }
        });
    }
}
