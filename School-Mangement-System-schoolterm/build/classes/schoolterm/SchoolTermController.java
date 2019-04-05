/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolterm;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXComboBox;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolTermController implements Initializable {

    private PreparedStatement ps;
    private ResultSet rs;
    private ObservableList obList, obListAcaYear;
    @FXML
    private TableView<TermTable> tableView;
    @FXML
    private TableColumn columnName;
    @FXML
    private TableColumn columnStart;
    @FXML
    private TableColumn columnEnd;
    @FXML
    private TableColumn columnAcaYear;
    @FXML
    private JFXComboBox comboTerm;
    @FXML
    private JFXComboBox comboAcaYear;
    @FXML
    private JFXButton butEdit, butDelete;
    private Stage stage;
    private Parent root;
    private TermTable selectedValue;
    private FXMLLoader loader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        butEdit.setDisable(true);
        butDelete.setDisable(true);

        try {

            obListAcaYear = FXCollections.observableArrayList();
            Bridge.comboReportAcaYear.setItems(obListAcaYear);
            Bridge.comboGraphyAcaYear.setItems(obListAcaYear);
            Bridge.comboStudentFeeAcaYear.setItems(obListAcaYear);

            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnName.setCellValueFactory(new PropertyValueFactory("name"));
            columnStart.setCellValueFactory(new PropertyValueFactory("start"));
            columnEnd.setCellValueFactory(new PropertyValueFactory("end"));
            columnAcaYear.setCellValueFactory(new PropertyValueFactory("acaYear"));

            //populate Table
            refreshTable();
            refreshObListAcaYear();

            tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TermTable> observable, TermTable oldValue, TermTable newValue) -> {
                selectedValue = newValue;
                butEdit.setDisable(false);
                butDelete.setDisable(false);
            });

            columnName.prefWidthProperty().bind(tableView.widthProperty().divide(4));
            columnStart.prefWidthProperty().bind(tableView.widthProperty().divide(4));
            columnEnd.prefWidthProperty().bind(tableView.widthProperty().divide(4));
            columnAcaYear.prefWidthProperty().bind(tableView.widthProperty().divide(4));

            Platform.runLater(() -> {
                stage = new Stage();
                stage.setResizable(false);
            });

            comboAcaYear.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    comboTerm.getItems().clear();

                    ps = SchoolDataBase.getCon().prepareStatement("Select Distinct Name From StudentTerm Where Academic_Year=?");
                    ps.setString(1, newValue.toString());
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        comboTerm.getItems().add(rs.getString("Name"));
                    }
                } catch (Exception e) {
                }
            });

            //Populate Combox with Terms and Academic Year
            comboBoxItems();

            displayCurrentTerm();

        } catch (Exception e) {
        }

    }

    private void displayCurrentTerm() throws SQLException {
        ps = SchoolDataBase.getCon().prepareStatement("Select * from CurrentTerm");
        rs = ps.executeQuery();

        if (rs.next()) {
            comboTerm.setValue(rs.getString("Term"));
            comboAcaYear.setValue(rs.getString("Academic_Year"));
        }
    }

    private void comboBoxItems() throws SQLException {
        comboAcaYear.getItems().clear();

        ps = SchoolDataBase.getCon().prepareStatement("Select distinct Academic_Year from StudentTerm");
        rs = ps.executeQuery();

        while (rs.next()) {
            comboAcaYear.getItems().add(rs.getString("Academic_Year"));
        }
        ps.close();
        rs.close();

        //incase user click add,edit,delete button without perfroming any action
        //set current Term and Year to their Combox
        //displayCurrentTerm();
    }

    private void refreshTable() throws SQLException {
        //Populate Term Table  
        obList.clear();
        ps = SchoolDataBase.getCon().prepareStatement("Select * from StudentTerm");
        rs = ps.executeQuery();

        while (rs.next()) {
            obList.add(new TermTable(rs.getString("name"), rs.getString("Starting"), rs.getString("Ending"), rs.getString("Academic_Year")));
        }
        ps.close();
        rs.close();
    }

    private void refreshObListAcaYear() throws SQLException {
        obListAcaYear.clear();
        ps = SchoolDataBase.getCon().prepareStatement("Select Distinct Academic_Year from StudentTerm order by ACADEMIC_YEAR asc");
        rs = ps.executeQuery();

        while (rs.next()) {
            obListAcaYear.add(rs.getString("Academic_Year"));
        }
        ps.close();
    }

    @FXML
    private void saveAction() {
        try {
            ps = SchoolDataBase.getCon().prepareStatement("Update CurrentTerm set Term=?,Academic_Year=?");
            ps.setString(1, comboTerm.getValue().toString());
            ps.setString(2, comboAcaYear.getValue().toString());
            ps.executeUpdate();
            ps.close();
            SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Current Term", "Saved");

            //this will change the currentTerm text to the recent one in the Main fxml controller
            //this act as a bridge to Student Management System Controller
            Bridge.label.setText(comboTerm.getValue() + " " + comboAcaYear.getValue());
        } catch (Exception e) {
        }
    }

    private void loadFxml(String fxml) throws IOException {
        loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
    }

    private void showFxml(Parent root, String title) {
        if (stage.getModality().equals(Modality.APPLICATION_MODAL)) {
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.showAndWait();
        } else {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(stage.getOwner());
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
    }

    @FXML
    private void addAction() {
        //when add stage lose focus refresh Current Term and AcademicYear Combo Box Items
        stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                if (oldValue) {
                    comboBoxItems();
                    displayCurrentTerm();
                }
            } catch (Exception e) {
            }
        });

        try {
            loadFxml("AddTerm.fxml");
            AddTermController add = loader.getController();
            add.initObList(obList, obListAcaYear);
            showFxml(root, "Add Term");
        } catch (Exception e) {
        }
    }

    @FXML
    private void editAction() {
        //when add stage lose focus refresh Current Term and AcademicYear Combo Box Items
        stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                if (oldValue) {
                    comboBoxItems();
                    displayCurrentTerm();
                    refreshObListAcaYear();
                    butEdit.setDisable(true);
                    butDelete.setDisable(true);
                }
            } catch (Exception e) {
            }
        });

        try {
            loadFxml("EditTerm.fxml");
            EditTermController edit = loader.getController();
            edit.getSelectedValue(selectedValue);
            showFxml(root, "Edit Term");
        } catch (Exception e) {
        }
    }

    @FXML
    private void deleteAction() {
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Delete Term", "Are you sure you want to delete?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");

            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes, no);

            Optional<ButtonType> op = SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps = SchoolDataBase.getCon().prepareStatement("Delete from StudentTerm where Starting=?");
                ps.setDate(1, Date.valueOf(selectedValue.getStart()));
                ps.executeUpdate();

                refreshTable();
                refreshObListAcaYear();

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Delete Term", "Delete Succesful");

                butEdit.setDisable(true);
                butDelete.setDisable(true);

                comboBoxItems();
                displayCurrentTerm();
            }
        } catch (Exception e) {
        }
    }
}
