/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolsalary;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.Date;
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
public class SchoolSalaryController implements Initializable {

    @FXML
    private VBox vBox;
    @FXML
    private ProgressIndicator progress;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private TableView<Salary> tableView;
    @FXML
    private TableColumn columnTeacher, columnSalary, columnPaidUnpaid, columnAmount;
    @FXML
    private JFXButton buttonSave;
    private PreparedStatement ps;
    private ResultSet rs;
    private ObservableList obList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnTeacher.setCellValueFactory(new PropertyValueFactory("teacher"));
            columnSalary.setCellValueFactory(new PropertyValueFactory("salary"));
            columnPaidUnpaid.setCellValueFactory(new PropertyValueFactory("paidUnpaid"));
            columnAmount.setCellValueFactory(new PropertyValueFactory("amount"));

            columnAmount.setCellFactory(TextFieldTableCell.forTableColumn());

            buttonSave.setDisable(true);
        } catch (Exception e) {
        }
    }

    private class GenerateTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            try {
                obList.clear();
                ps = SchoolDataBase.getCon().prepareStatement("Select * From Salary Where Date=?");
                ps.setDate(1, Date.valueOf(datePicker.getValue()));
                rs = ps.executeQuery();

                while (rs.next()) {
                    obList.add(new Salary(rs.getString("Teacher"), rs.getString("Salary"), rs.getString("PaidUnpaid"), rs.getString("Amount")));
                }

                /*
                If the first query retures an empty table
                just populate Teacher Column with Teacher Names
                Salary Column with Teacher's Salary
                Paid/Unpaid Column with "Unpaid"
                Because if the first query is empty
                That means no record of Salary has been saved for
                That particuler Date
                 */
                if (obList.isEmpty()) {
                    ps = SchoolDataBase.getCon().prepareStatement("Select Distinct Name,Salary From Teacher");
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        obList.add(new Salary(rs.getString("Name"), rs.getString("Salary"), "Unpaid", ""));
                    }
                }
            } catch (Exception e) {
                vBox.setDisable(false);
                progress.setVisible(false);
                
                Platform.runLater(() -> {
                    SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Error! Can't Generate Salary");
                });
            }

            return null;
        }

    }

    @FXML
    private void generateSalary() {
        if (datePicker.getValue() == null) {
            SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Please Select Date");
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
    }

    private class SaveTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            try {
                ps = SchoolDataBase.getCon().prepareStatement("Delete From Salary Where Date=?");
                ps.setDate(1, Date.valueOf(datePicker.getValue()));
                ps.executeUpdate();
                
                ps = SchoolDataBase.getCon().prepareStatement("Insert Into Salary Values(?,?,?,?,?)");
                for (Object data : tableView.getItems()) {
                    ps.setDate(1, Date.valueOf(datePicker.getValue()));
                    ps.setString(2, columnTeacher.getCellData(data).toString());
                    ps.setString(3, columnSalary.getCellData(data).toString());
                    ps.setString(4, columnPaidUnpaid.getCellData(data).toString());
                    ps.setInt(5, Integer.parseInt(columnAmount.getCellData(data).toString()));
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (Exception e) {
                vBox.setDisable(false);
                progress.setVisible(false);
                
                Platform.runLater(() -> {
                    SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Error! Can't Save Salary");
                });
            }

            return null;
        }

    }

    @FXML
    private void saveSalary() {
        vBox.setDisable(true);
        progress.setVisible(true);

        SaveTask task = new SaveTask();
        task.setOnSucceeded((event) -> {
            vBox.setDisable(false);
            progress.setVisible(false);

            SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Salary", "Saved");
        });
        new Thread(task).start();
    }

}
