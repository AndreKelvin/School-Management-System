/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldailyexpenses;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class AddExpensesController implements Initializable {

    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXTextField textDescrip;
    @FXML
    private JFXTextField textAmount;
    private PreparedStatement ps;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Force the text field to numeric only
        textAmount.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                textAmount.setText(newValue.replaceAll("[^\\d]", ""));
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    @FXML
    private void saveExpenses() {
        try {
            String descrip = textDescrip.getText().trim();
            String amount = textAmount.getText().trim();

            if (datePicker.getValue() == null || descrip.isEmpty() || amount.isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input");
            } else {
                ps = SchoolDataBase.getCon().prepareStatement("Insert Into Daily_Expenses Values(?,?,?)");
                ps.setDate(1, Date.valueOf(datePicker.getValue()));
                ps.setString(2, descrip);
                ps.setInt(3, Integer.parseInt(amount));
                ps.executeUpdate();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Saved");
                textAmount.clear();
                textDescrip.clear();
                datePicker.setValue(null);
            }
        } catch (Exception e) {
        }
    }

}
