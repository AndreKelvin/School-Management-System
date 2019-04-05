/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentclass;

import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import schoolalertdialog.SchoolAlertDialog;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class EditClassController {

    @FXML private JFXTextField textFieldEdit;
    private ClassTable selectedClass;
    private PreparedStatement ps;
    
    public void getSelectedClass(ClassTable selectedClass){
        this.selectedClass=selectedClass;
        textFieldEdit.setText(selectedClass.getClassName());
    }
    
    @FXML
    private void editAction() {
        try {
            String value=textFieldEdit.getText().trim().toUpperCase();
            if (value.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }
            else{
                ps=schooldatabase.SchoolDataBase.getCon().prepareStatement("update STUDENTCLASS set class=? where class=?");
                ps.setString(1, value);
                ps.setString(2, selectedClass.getClassName());
                ps.executeUpdate();

                selectedClass.setClassName(value);
                ps.close();

                textFieldEdit.getScene().getWindow().hide();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Class", "Class Edited");
            }
        } catch (Exception e) {}
    }
}
