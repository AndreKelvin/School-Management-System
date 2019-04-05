/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolterm;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class EditTermController {

    @FXML private JFXTextField textfieldname;
    @FXML private JFXTextField textfieldAcaYear;
    @FXML private JFXDatePicker dateStart;
    @FXML private JFXDatePicker dateEnd;
    private PreparedStatement ps;
    private ResultSet rs;
    private TermTable tTable;;
    private String startOn;
    
    //get selected value form SchoolTermController
    public void getSelectedValue(TermTable selectedValue) {
        tTable=selectedValue;
        startOn=tTable.getStart();
        try {
            ps=SchoolDataBase.getCon().prepareStatement("Select * from StudentTerm where Starting=?");
            ps.setString(1, startOn);
            rs=ps.executeQuery();
            
            while(rs.next()){
                textfieldname.setText(rs.getString("name"));
                dateStart.setValue(rs.getDate("Starting").toLocalDate());
                dateEnd.setValue(rs.getDate("Ending").toLocalDate());
                textfieldAcaYear.setText(rs.getString("Academic_Year"));
            }
        } catch (Exception e) {}
    } 

    @FXML
    private void editAction(ActionEvent event) {
        try {
            String name=textfieldname.getText().trim().toUpperCase();
            String year=textfieldAcaYear.getText();
            String sDate=dateStart.getValue().toString();
            String eDate=dateEnd.getValue().toString();
            
            //tTable=new TermTable(name, sDate, eDate, year);
            
            if (name.isEmpty()||year.isEmpty()||sDate.isEmpty()||eDate.isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Term", "Invalid Input");
            }
            else{
                ps=SchoolDataBase.getCon().prepareStatement("Update StudentTerm set Name=?,Starting=?,Ending=?,Academic_Year=? where Starting=?");
                ps.setString(1, name);
                ps.setDate(2, Date.valueOf(sDate));
                ps.setDate(3, Date.valueOf(eDate));
                ps.setString(4, year);
                ps.setString(5, startOn);
                ps.executeUpdate();
                
                //change the former values and display it in table
                tTable.setName(name);
                tTable.setStart(sDate);
                tTable.setEnd(eDate);
                tTable.setAcaYear(year);
                
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Edit Term", "Change Succesful");
                
                dateEnd.getScene().getWindow().hide();
            }
        } catch (Exception e) {}
    }
}
