/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolfees;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
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
public class EditFeeController implements Initializable {

    private Fee selectedValue;
    @FXML private JFXTextField textFieldFee,textFieldClass;
    private PreparedStatement ps;
    private ResultSet rs;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void getSelectedValue(Fee selectedValue){
        try {
            this.selectedValue=selectedValue;
            ps=SchoolDataBase.getCon().prepareStatement("Select * From SchoolFee Where Fee=? and Class=?");
            ps.setString(1, selectedValue.getFee());
            ps.setString(2, selectedValue.getClas());
            rs=ps.executeQuery();
            
            while(rs.next()){
                textFieldClass.setText(rs.getString("Class"));
                textFieldFee.setText(rs.getString("Fee"));
            }
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction() {
        try {
            String fee=textFieldFee.getText().trim().toUpperCase();
            String clas=textFieldClass.getText().trim().toUpperCase();
            if (fee.isEmpty()||clas.isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Edit Fee", "Inviled Input");
            }
            else{
                ps=SchoolDataBase.getCon().prepareStatement("Update SchoolFee Set Class=?,Fee=? Where Class=? and Fee=?");
                ps.setString(1, clas);
                ps.setString(2, fee);
                ps.setString(3, selectedValue.getClas());
                ps.setString(4, selectedValue.getFee());
                ps.executeUpdate();

                selectedValue.setClas(clas);
                selectedValue.setFee(fee);
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Edit Fee", "Change Successful");
                textFieldClass.getScene().getWindow().hide();
            }
        } catch (Exception e) {}
    }
}
