/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldailyexpenses;

import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
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
public class EditExpensesController implements Initializable {

    @FXML
    private JFXTextField textDescrip,textAmount;
    private PreparedStatement ps;
    private ResultSet rs;
    private Expenses selectedValue;
    private LocalDate selectedDate;

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
    
    public void setSelectedValue(Expenses selectedValue, LocalDate selectedDate){
        this.selectedValue=selectedValue;
        this.selectedDate=selectedDate;
        
        textDescrip.setText(selectedValue.getDescription());
        textAmount.setText(selectedValue.getAmount()+"");
    }

    @FXML
    private void editExpenses() {
        try {
            String descrip = textDescrip.getText().trim();
            String amount = textAmount.getText().trim();

            if (descrip.isEmpty() || amount.isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input");
            } else {
                ps = SchoolDataBase.getCon().prepareStatement("Update Daily_Expenses Set Description=?,Amount=? Where Date=? and Description=?");
                ps.setString(1, descrip);
                ps.setInt(2, Integer.parseInt(amount));
                ps.setDate(3, Date.valueOf(selectedDate));
                ps.setString(4, selectedValue.getDescription());
                ps.executeUpdate();
                
                selectedValue.setDescription(descrip);
                selectedValue.setAmount(Integer.parseInt(amount));

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Changes Saved");
                //textAmount.getScene().getWindow().hide();
            }
        } catch (Exception e) {
        }
    }
    
}
