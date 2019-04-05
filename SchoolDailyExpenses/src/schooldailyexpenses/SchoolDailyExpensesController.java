/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldailyexpenses;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolDailyExpensesController implements Initializable {

    @FXML
    private VBox vBox;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXButton buttonEdit, buttonDelete;
    @FXML
    private TableView<Expenses> tableView;
    @FXML
    private TableColumn columnDescription, columnAmount;
    @FXML
    private Label labelTotal;
    @FXML
    private ProgressIndicator progress;
    private ObservableList obList;
    private PreparedStatement ps;
    private ResultSet rs;
    private Expenses selectedValue;
    private int total;
    private Stage stage;
    private Parent parent;
    private FXMLLoader loader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnDescription.setCellValueFactory(new PropertyValueFactory("description"));
            columnAmount.setCellValueFactory(new PropertyValueFactory("amount"));

            buttonEdit.setDisable(true);
            buttonDelete.setDisable(true);

            tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                buttonEdit.setDisable(false);
                buttonDelete.setDisable(false);

                selectedValue = newValue;
            });

            Platform.runLater(() -> {
                stage=new Stage();
                stage.setResizable(false);
            });
        } catch (Exception e) {
        }
    }

    private class GenerateTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            try {
                displayExpenses();
            } catch (Exception e) {
                vBox.setDisable(false);
                progress.setVisible(false);

                Platform.runLater(() -> {
                    SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Error! Can't Generate Expenses");
                });
            }

            return null;
        }

    }

    private void displayExpenses() throws SQLException {
        obList.clear();
        total=0;
        
        ps = SchoolDataBase.getCon().prepareStatement("Select * From Daily_Expenses Where Date=?");

        /*
        This if below is a fucked up solution
        i am trying to display expenses when Add Dialog is closed
        if no Date is selected and user open Add Dialog then close it
        it will throw a null pointer exception
         */
        if (datePicker.getValue() == null) {
        } else {
            ps.setDate(1, Date.valueOf(datePicker.getValue()));
        }
        rs = ps.executeQuery();

        while (rs.next()) {
            obList.add(new Expenses(rs.getString("description"), rs.getInt("amount")));
            total += rs.getInt("amount");
        }
        
        Platform.runLater(() -> {
            labelTotal.setText(new DecimalFormat(",000").format(total));
        });
    }

    @FXML
    private void generateExpenses() {
        if (datePicker.getValue() == null) {
            SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Please Select Date");
        } else {
            vBox.setDisable(true);
            progress.setVisible(true);

            GenerateTask task = new GenerateTask();
            task.setOnSucceeded((event) -> {
                vBox.setDisable(false);
                progress.setVisible(false);
            });
            new Thread(task).start();
        }
    }

    private void loadFxml(String fxml) throws IOException {
        loader = new FXMLLoader(getClass().getResource(fxml));
        parent = loader.load();
    }

    private void showFxml(Parent parent, String title) {
        if (stage.getModality().equals(Modality.APPLICATION_MODAL)) {
            stage.setScene(new Scene(parent));
            stage.setTitle(title);
            stage.showAndWait();
        } else {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(stage.getOwner());
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(new Scene(parent));
            stage.setTitle(title);
            stage.showAndWait();
        }
    }

    @FXML
    private void openAddDialog() {
        try {
            loadFxml("AddExpenses.fxml");
            showFxml(parent, "Add Expenses");
        } catch (IOException ex) {
        }
        
        stage.setOnCloseRequest((event) -> {
            try {
                displayExpenses();
            } catch (SQLException ex) {
            }
        });
    }

    @FXML
    private void openEditDialog() {
        try {
            loadFxml("EditExpenses.fxml");
            EditExpensesController edit = loader.getController();
            edit.setSelectedValue(selectedValue, datePicker.getValue());
            showFxml(parent, "Edit Expenses");
        } catch (IOException ex) {
        }
        
        stage.setOnCloseRequest((event) -> {
            try {
                buttonDelete.setDisable(true);
                buttonEdit.setDisable(true);
                
                displayExpenses();
            } catch (Exception e) {
            }
        });
    }

    @FXML
    private void openDeleteDialog() {
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Daily Expenses", "Are you sure you want to Delete?");
        
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");

           SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);

            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("Delete From Daily_Expenses Where Date=? and Description=?");
                ps.setDate(1, Date.valueOf(datePicker.getValue()));
                ps.setString(2, selectedValue.getDescription());
                ps.executeUpdate();
                
                SchoolAlertDialog.showDeleteAlert(Alert.AlertType.CONFIRMATION, "Daily Expenses", "Deleted");
                
                displayExpenses();
                
                buttonDelete.setDisable(true);
                buttonEdit.setDisable(true);
            }
        } catch (Exception e) {e.printStackTrace();
        }
    }

}
