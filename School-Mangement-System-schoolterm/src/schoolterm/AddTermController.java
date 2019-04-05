/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolterm;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
public class AddTermController implements Initializable {

    @FXML private JFXTextField textfieldname;
    @FXML private JFXTextField textfieldAcaYear;
    @FXML private JFXDatePicker dateStart;
    @FXML private JFXDatePicker dateEnd;
    private TermTable tTable;
    private ObservableList<TermTable> obList;
    private ObservableList obListAcaYear;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    //get ObservableList from SchoolTermController to add new values to list
    public void initObList(ObservableList<TermTable> obList, ObservableList obListAcaYear){
        this.obList=obList;
        this.obListAcaYear=obListAcaYear;
    }
    
    @FXML
    void addAction(ActionEvent event) {
        try {
            String name=textfieldname.getText().trim().toUpperCase();
            String year=textfieldAcaYear.getText();
            
            if (name.isEmpty()||year.isEmpty()||dateStart.getValue()==null||dateEnd.getValue()==null) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Term", "Invalid Input");
            }
            else{
                String sDate=dateStart.getValue().toString();
                String eDate=dateEnd.getValue().toString();
                
                //new instance of TermTable to make new added values display immediatlly on the Table
                tTable=new TermTable(name, sDate, eDate, year);
                
                PreparedStatement ps=SchoolDataBase.getCon().prepareStatement("Insert into Studentterm values(?,?,?,?)");
                ps.setString(1, name);
                ps.setDate(2, Date.valueOf(sDate));
                ps.setDate(3, Date.valueOf(eDate));
                ps.setString(4, year);
                ps.executeUpdate();
                
                obList.add(tTable);
                obListAcaYear.add(year);
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Add Term", "Added");
                
                textfieldAcaYear.clear();
                textfieldname.clear();
                dateEnd.setValue(null);
                dateStart.setValue(null);
            }
        } catch (Exception e) {}
    }
}
