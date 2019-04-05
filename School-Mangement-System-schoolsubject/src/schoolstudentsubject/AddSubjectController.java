/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentsubject;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.sql.PreparedStatement;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class AddSubjectController {

    @FXML private JFXTextField textfieldAddSub;
    @FXML private JFXComboBox comboAddClass; 
    private ObservableList obList;
   
    public void getList(ObservableList list, ObservableList obList) {
        comboAddClass.setItems(list);
        this.obList=obList;
    }    
    
    @FXML
    private void addAction(ActionEvent event) {
        //Add Subject
        String sub=textfieldAddSub.getText().trim().toUpperCase();
        
        if(sub.isEmpty()||comboAddClass.getValue()==null){
            SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Subject", "Invalid Input");
        }
        else{
            try {
                PreparedStatement ps=SchoolDataBase.getCon().prepareStatement("Insert into STUDENTSUBJECT (Subject,Class) values(?,?)");
                ps.setString(1, sub);
                ps.setString(2, comboAddClass.getValue().toString());
                ps.executeUpdate();
                
                //obList.add(sub);

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Subject", "Subject Added");

                textfieldAddSub.clear();
                comboAddClass.setValue(null);
            } catch (Exception e) {}
        }
    }
}
