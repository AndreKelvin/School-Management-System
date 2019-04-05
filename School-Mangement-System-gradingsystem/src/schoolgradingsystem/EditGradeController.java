/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgradingsystem;

import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class EditGradeController implements Initializable {

    @FXML private JFXTextField textFieldScore,textFieldGrade,textFieldRemark;
    private Grade selectedGrade;
    private PreparedStatement ps;
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Force the text field to letters only
        textFieldGrade.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\sa-zA-Z*")){
                textFieldGrade.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });
        
        textFieldRemark.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\sa-zA-Z*")){
                textFieldRemark.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });
    }
    
    public void getSelectedGrade(Grade selectedGrade) {
        try {
            this.selectedGrade=selectedGrade;
            ps=SchoolDataBase.getCon().prepareStatement("Select * from GradingSystem where score=?");
            ps.setString(1, this.selectedGrade.toString());
            ResultSet rs=ps.executeQuery();
            
            while(rs.next()){
                textFieldScore.setText(rs.getString("Score"));
                textFieldGrade.setText(rs.getString("Grade"));
                textFieldRemark.setText(rs.getString("Remark"));
            }
        } catch (Exception e) {}
    }    
    
    @FXML
    private void saveAction() {
        String score=textFieldScore.getText();
        String grade=textFieldGrade.getText().toUpperCase();
        String remark=textFieldRemark.getText();
        
        if (score.isEmpty()||grade.isEmpty()||remark.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
        }
        else{
            try {
                ps=SchoolDataBase.getCon().prepareStatement("update GradingSystem set Score=?,Grade=?,Remark=? where score=?");
                ps.setString(1, score);
                ps.setString(2, grade);
                ps.setString(3, remark);
                ps.setString(4, selectedGrade.toString());
                ps.executeUpdate();
                
                selectedGrade.setScore(score);
                selectedGrade.setGrade(grade);
                selectedGrade.setRemark(remark);

                textFieldGrade.getScene().getWindow().hide();
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Edit Grade", "Edited");
            } catch (Exception e) {}
        }
    }
}
