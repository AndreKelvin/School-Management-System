/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentdetails;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class StudentAcademicReportController {
    
    @FXML private Label labelStdName,labelClass,labelTerm,labelAcaYear;
    @FXML private TableView<AcaReport> tableView;
    @FXML private TableColumn columnSub,columnConAss,columnExam,columnTotal,columnGrade;
    
    public void getValues(String selectedName, String comboClass, String selectedTerm, String selectedAcaYear){
        labelStdName.setText(selectedName);
        labelClass.setText(comboClass);
        labelTerm.setText(selectedTerm);
        labelAcaYear.setText(selectedAcaYear);
        
        ObservableList<AcaReport> obList=FXCollections.observableArrayList();
        tableView.setItems(obList);
        
        columnSub.setCellValueFactory(new PropertyValueFactory("sub"));
        columnConAss.setCellValueFactory(new PropertyValueFactory("conAss"));
        columnExam.setCellValueFactory(new PropertyValueFactory("exam"));
        columnTotal.setCellValueFactory(new PropertyValueFactory("total"));
        columnGrade.setCellValueFactory(new PropertyValueFactory("grade"));
        
        try {
            PreparedStatement ps=SchoolDataBase.getCon().prepareStatement("SELECT SUBJECT,CONTINUES_ASSESSMENT,EXAM,TOTAL,GRADE FROM "
                                                                                       + "SCORESHEET where STUDENT_NAME=? and CLASS=? and TERM=? and Academic_Year=?");
            ps.setString(1, selectedName);
            ps.setString(2, comboClass);
            ps.setString(3, selectedTerm);
            ps.setString(4, selectedAcaYear);
            ResultSet rs=ps.executeQuery();
            while (rs.next()) {                
                obList.add(new AcaReport(rs.getString("Subject"), rs.getInt("CONTINUES_ASSESSMENT"), rs.getInt("EXAM"), rs.getInt("TOTAL"), rs.getString("GRADE")));
            }
        } catch (Exception e) {}
    }
}
