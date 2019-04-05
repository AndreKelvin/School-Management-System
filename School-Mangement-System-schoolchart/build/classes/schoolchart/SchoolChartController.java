/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolchart;

import bridge.Bridge;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.Alert;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolChartController implements Initializable{
    
    @FXML private BarChart<?, ?> barChart;
    @FXML private CategoryAxis x;
    @FXML private NumberAxis y;
    @FXML private JFXComboBox comboClass,comboTerm,comboYear;
    private PreparedStatement ps;
    private ResultSet rs;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ps=SchoolDataBase.getCon().prepareStatement("Select * from StudentClass");
            rs=ps.executeQuery();
            while(rs.next()){
                comboClass.getItems().add(rs.getString("Class"));
            }
            ps.close();
            
            ps=schooldatabase.SchoolDataBase.getCon().prepareStatement("Select distinct Academic_Year from StudentTerm");
            rs=ps.executeQuery();
            while(rs.next()){
                comboYear.getItems().add(rs.getString("Academic_Year"));
            }
            ps.close();
            
            comboYear.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    comboTerm.getItems().clear();
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct name from StudentTerm Where Academic_Year=?");
                    ps.setString(1, newValue.toString());
                    rs=ps.executeQuery();
                    while(rs.next()){
                        comboTerm.getItems().add(rs.getString("name"));
                    }
                    ps.close();
                } catch (Exception e) {}
            });
            
            //pass the combo box
            Bridge.comboGraphyClass=comboClass;
            Bridge.comboGraphyAcaYear=comboYear;
            
            comboYear.setOnShowing((Event event) -> {
                comboYear.getItems().clear();
                try {
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct Academic_Year from StudentTerm");
                    rs=ps.executeQuery();
                    while(rs.next()){
                        comboYear.getItems().add(rs.getString("Academic_Year"));
                    }
                    ps.close();
                } catch (Exception e) {}
            });
        } catch (Exception e) {}
    }
    
    @FXML
    private void genAction() {
        try {
            XYChart.Series ser=new XYChart.Series();
            if (comboClass.getValue()==null||comboTerm.getValue()==null||comboYear.getValue()==null) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Graphical Report", "Invalid Input");
            }
            else{
                ser.setName("Report Summary");
                ps=SchoolDataBase.getCon().prepareStatement("Select Total_Student,Total_Pass,Total_Fail,Total_Boys,Boys_Pass,Boys_Fail,"
                        + " Total_Girls,Girls_Pass,Girls_Fail from Summary Where Class=? and Term=? and Academic_Year=?");
                ps.setString(1,comboClass.getValue().toString());
                ps.setString(2,comboTerm.getValue().toString());
                ps.setString(3,comboYear.getValue().toString());
                rs=ps.executeQuery();
                barChart.getData().clear();
                while(rs.next()){
                    ser.getData().add(new XYChart.Data("Number Of Student", rs.getInt("Total_Student")));
                    ser.getData().add(new XYChart.Data("Number Of Pass", rs.getInt("Total_Pass")));
                    ser.getData().add(new XYChart.Data("Number Of Fail", rs.getInt("Total_Fail")));

                    ser.getData().add(new XYChart.Data("Number Of Boys", rs.getInt("Total_Boys")));
                    ser.getData().add(new XYChart.Data("Number Of Boys Pass", rs.getInt("Boys_Pass")));
                    ser.getData().add(new XYChart.Data("Number Of Boys Fail", rs.getInt("Boys_Fail")));

                    ser.getData().add(new XYChart.Data("Number Of Girls", rs.getInt("Total_Girls")));
                    ser.getData().add(new XYChart.Data("Number Of Girls Pass", rs.getInt("Girls_Pass")));
                    ser.getData().add(new XYChart.Data("Number Of Girls Fail", rs.getInt("Girls_Fail")));
                }
                barChart.getData().addAll(ser);;
            } 
        }catch (Exception e) {}
    }
}
