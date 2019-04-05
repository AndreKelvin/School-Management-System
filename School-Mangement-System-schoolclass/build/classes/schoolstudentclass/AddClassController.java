/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentclass;

import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class AddClassController {

    @FXML private JFXTextField textFieldAdd;
    private ObservableList obList;
    private PreparedStatement ps;
    
    public void getObList(ObservableList list) {
        obList=list;
    }    
    
    @FXML
    private void addAction() {
        try {
            String add=textFieldAdd.getText().trim().toUpperCase();
            if (add.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }
            else{
                ps=SchoolDataBase.getCon().prepareStatement("insert into STUDENTCLASS values(?)");
                ps.setString(1, add);
                ps.executeUpdate();

                obList.add(new ClassTable(add));
                ps.close();
                                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Class", "Class Added");
                textFieldAdd.clear();
            }
        } catch (Exception e) {}
    }
}
