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
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
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
public class InputGradeController implements Initializable {

    @FXML private JFXTextField textFieldScore,textFieldGrade,textFieldRemark;
    private ObservableList obList;
    private PreparedStatement ps;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Force the text field to letters only
//        textFieldGrade.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
//            if(!newValue.matches("\\sa-zA-Z*")){
//                textFieldGrade.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
//            }
//        });
        
        textFieldRemark.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\sa-zA-Z*")){
                textFieldRemark.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });
    }
    
    public void getObList(ObservableList obList) {
        this.obList=obList;
    }    
 
    @FXML
    private void inputAction() {
        try {
            String score=textFieldScore.getText().trim();
            String grade=textFieldGrade.getText().trim().toUpperCase();
            String remark=textFieldRemark.getText().trim();

            if (score.isEmpty()||grade.isEmpty()||remark.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }
            else{
                obList.add(new Grade(score, grade, remark));
                ps=SchoolDataBase.getCon().prepareStatement("Insert Into GradingSystem values(?,?,?)");
                ps.setString(1, score);
                ps.setString(2, grade);
                ps.setString(3, remark);
                ps.executeUpdate();
                ps.close();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Add Grade", "Added");
                
                textFieldGrade.clear();
                textFieldRemark.clear();
                textFieldScore.clear();
            }
        } catch (Exception e) {
        }
    }
}
