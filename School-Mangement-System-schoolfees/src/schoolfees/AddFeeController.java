/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolfees;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
public class AddFeeController implements Initializable {

    @FXML private JFXTextField textFieldFee,textFieldClass;
    private PreparedStatement ps;
    private ObservableList<Fee> obList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void getObList(ObservableList obList){
        this.obList=obList;
    }
    
    @FXML
    private void addAction(){
        try {
            String fee=textFieldFee.getText().trim().toUpperCase();
            String clas=textFieldClass.getText().trim().toUpperCase();
            if (fee.isEmpty()||clas.isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Fee", "Inviled Input");
            }
            else{
                ps=SchoolDataBase.getCon().prepareStatement("Insert into SchoolFee Values(?,?)");
                ps.setString(1, clas);
                ps.setString(2, fee);
                ps.executeUpdate();

                obList.add(new Fee(clas, fee));
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Add Fee", "Add Successful");
            }
        } catch (Exception e) {}
    }
}
