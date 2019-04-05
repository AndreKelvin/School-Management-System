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
public class EditSubjectController {

    @FXML private JFXTextField textfieldEditSub;
    @FXML private JFXComboBox comboEditClass;
    private String selectedSub;
    
    public void getListandSelectedSub(ObservableList list, String selectedSub, Object comboClassValue) {
        comboEditClass.setValue(comboClassValue);
        comboEditClass.setItems(list);
        textfieldEditSub.setText(selectedSub);
        this.selectedSub=selectedSub;
    }    
    
    @FXML
    private void editAction(ActionEvent event) {
        try {
            //Edit Subject
            String sub=textfieldEditSub.getText().trim().toUpperCase();
            
            if (sub.isEmpty()||comboEditClass.getValue()==null) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Subject", "Invalid Input");
            }
            else{
                PreparedStatement ps=SchoolDataBase.getCon().prepareStatement("Update STUDENTSUBJECT set Subject=?,Class=? where Subject=? and class=?");
                ps.setString(1, sub);
                ps.setString(2, comboEditClass.getValue().toString());
                ps.setString(3, selectedSub);
                ps.setString(4, comboEditClass.getValue().toString());
                ps.executeUpdate();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Subject", "Subject Edited");

                textfieldEditSub.getScene().getWindow().hide();
            }
        } catch (Exception e) {}
    }
}
