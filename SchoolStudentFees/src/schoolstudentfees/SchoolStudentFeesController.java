/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentfees;

import bridge.Bridge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolStudentFeesController implements Initializable {

    @FXML
    private VBox vBox;
    @FXML
    private ProgressIndicator progress;
    @FXML
    private JFXComboBox comboClass, comboAcaYear, comboTerm;
    @FXML
    private TableView<Fee> tableView;
    @FXML
    private TableColumn columnName, columnPaidUnpaid, columnAmount, columnBalance;
    @FXML
    private JFXButton buttonSave;
    private PreparedStatement ps;
    private ResultSet rs;
    private ObservableList obList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            
            ps = SchoolDataBase.getCon().prepareStatement("Select * From StudentClass");
            rs = ps.executeQuery();

            while (rs.next()) {
                comboClass.getItems().add(rs.getString("Class"));
            }

            ps = SchoolDataBase.getCon().prepareStatement("Select Distinct Academic_Year From StudentTerm");
            rs = ps.executeQuery();

            while (rs.next()) {
                comboAcaYear.getItems().add(rs.getString("Academic_Year"));
            }

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

            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnName.setCellValueFactory(new PropertyValueFactory("name"));
            columnPaidUnpaid.setCellValueFactory(new PropertyValueFactory("paidUnpaid"));
            columnAmount.setCellValueFactory(new PropertyValueFactory("amount"));
            columnBalance.setCellValueFactory(new PropertyValueFactory("balance"));

            columnAmount.setCellFactory(TextFieldTableCell.forTableColumn());

            buttonSave.setDisable(true);
            
            //Pass Class and Academic Combo box
            Bridge.comboStudentFeeClass=comboClass;
            Bridge.comboStudentFeeAcaYear=comboAcaYear;
            
        } catch (Exception e) {
        }
    }

    private class GenerateTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            try {
                obList.clear();

                ps = SchoolDataBase.getCon().prepareStatement("Select Name,Paid_Unpaid,Amount,Balance From Student_Fee Where Class=? and Term=? and Academic_Year=?");
                ps.setString(1, comboClass.getValue().toString());
                ps.setString(2, comboTerm.getValue().toString());
                ps.setString(3, comboAcaYear.getValue().toString());
                rs = ps.executeQuery();

                while (rs.next()) {
                    obList.add(new Fee(rs.getString("Name"), rs.getString("Paid_Unpaid"), rs.getInt("Amount") + "", rs.getInt("Balance") + "", comboClass.getValue().toString()));
                }

                /*
                If the first query retures an empty table
                just populate Name Column with Student Names
                Paid/Unpaid Column with "Unpaid"
                Amount and Balance with 0
                Because if the first query is empty
                That means no record of Fees has been saved for
                Those particuler Students in that Class,Term,Year
                 */
                if (obList.isEmpty()) {
                    ps = SchoolDataBase.getCon().prepareStatement("Select Name From StudentInfo Where Class=?");
                    ps.setString(1, comboClass.getValue().toString());
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        obList.add(new Fee(rs.getString("Name"), "Unpaid", "0", "0", comboClass.getValue().toString()));
                    }
                }
            } catch (Exception e) {
                vBox.setDisable(false);
                progress.setVisible(false);

                Platform.runLater(() -> {
                    SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Error! Can't Generate Fee");
                });
            }

            return null;
        }

    }

    @FXML
    private void generateFees() {
        try {
            if (comboClass.getValue() == null || comboAcaYear.getValue() == null || comboTerm.getValue() == null) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Invalid input");
            } else {
                vBox.setDisable(true);
                progress.setVisible(true);

                GenerateTask task = new GenerateTask();
                task.setOnSucceeded((event) -> {
                    vBox.setDisable(false);
                    progress.setVisible(false);

                    buttonSave.setDisable(false);
                });
                new Thread(task).start();
            }
        } catch (Exception e) {
        }
    }

    private class SaveTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            try {
                ps = SchoolDataBase.getCon().prepareStatement("Delete From Student_Fee Where Class=?");
                ps.setString(1, comboClass.getValue().toString());
                ps.executeUpdate();

                ps = SchoolDataBase.getCon().prepareStatement("Insert Into Student_Fee Values(?,?,?,?,?,?,?)");
                for (Object data : tableView.getItems()) {
                    ps.setString(1, comboClass.getValue().toString());
                    ps.setString(2, columnName.getCellData(data).toString());
                    ps.setString(3, columnPaidUnpaid.getCellData(data).toString());
                    ps.setString(4, comboTerm.getValue().toString());
                    ps.setString(5, comboAcaYear.getValue().toString());
                    ps.setString(6, columnAmount.getCellData(data).toString());
                    ps.setString(7, columnBalance.getCellData(data).toString());
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (Exception e) {
                vBox.setDisable(false);
                progress.setVisible(false);

                Platform.runLater(() -> {
                    SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Error! Can't Save Fee");
                });
            }

            return null;
        }

    }

    @FXML
    private void saveFees() {
        try {
            vBox.setDisable(true);
            progress.setVisible(true);

            SaveTask task = new SaveTask();
            task.setOnSucceeded((event) -> {
                vBox.setDisable(false);
                progress.setVisible(false);

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Fee", "Saved");
            });
            new Thread(task).start();

        } catch (Exception e) {
        }
    }

}
