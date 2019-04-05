/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolscoresheet;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import bridge.Bridge;
import java.awt.Toolkit;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolScoreSheetController implements Initializable {

    @FXML
    private JFXComboBox comboClass, comboSub;
    @FXML
    private TableView<Score> tableView;
    @FXML
    private TableColumn columnStudID, columnStudName, columnSex, columnConAssess, columnEaxm, columnTotal, columnGrade;
    @FXML
    private JFXButton butSave, butUpdate;
    private PreparedStatement ps, ps2;
    private ResultSet rs;
    private ObservableList<Score> obList;
    private List curTermList, sList;
    private Statement stm;
    @FXML
    private StackPane stackPane;
    @FXML
    private VBox vBox;
    @FXML
    private ProgressIndicator progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        butSave.setDisable(true);
        butUpdate.setDisable(true);

        //pass the combo box to Bridge score sheet combo
        Bridge.comboScoreSheetClass = comboClass;

        try {
            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnConAssess.setCellValueFactory(new PropertyValueFactory("continuesAssessment"));
            columnEaxm.setCellValueFactory(new PropertyValueFactory("exam"));
            columnGrade.setCellValueFactory(new PropertyValueFactory("grade"));
            columnStudID.setCellValueFactory(new PropertyValueFactory("studentID"));
            columnStudName.setCellValueFactory(new PropertyValueFactory("studnetName"));
            columnSex.setCellValueFactory(new PropertyValueFactory("sex"));
            columnTotal.setCellValueFactory(new PropertyValueFactory("total"));

            //make Continues Assessment and Exam Column Editable
            columnConAssess.setCellFactory(TextFieldTableCell.forTableColumn());
            columnEaxm.setCellFactory(TextFieldTableCell.forTableColumn());

            //pupolate Class combo boxes
            ps = SchoolDataBase.getCon().prepareStatement("Select * from StudentClass");
            rs = ps.executeQuery();
            while (rs.next()) {
                comboClass.getItems().add(rs.getString("Class"));
            }
            ps.close();

            sList = new ArrayList();
            ps = SchoolDataBase.getCon().prepareStatement("Select Score from GradingSystem");
            rs = ps.executeQuery();
            while (rs.next()) {
                sList.add(rs.getString("Score").substring(2, 4));
            }
            ps.close();

        } catch (Exception e) {
        }

        try {

            //pupolate Subject combo boxes
            comboClass.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    comboSub.setValue(null);
                    comboSub.getItems().clear();
                    ps = SchoolDataBase.getCon().prepareStatement("Select distinct Subject from StudentSubject where class=?");
                    ps.setString(1, newValue.toString());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        comboSub.getItems().add(rs.getString("Subject"));
                    }
                    ps.close();
                    rs.close();
                } catch (Exception e) {
                }
            });

            //populate table
            comboSub.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                butSave.setDisable(true);
                butUpdate.setDisable(false);

                try {
                    obList.clear();
                    //get current term and year from db
                    curTermList = new ArrayList();
                    ps = SchoolDataBase.getCon().prepareStatement("Select * from CurrentTerm");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        curTermList.add(rs.getString("Term"));
                        curTermList.add(rs.getString("Academic_Year"));
                    }
                    ps.close();
                    rs.close();

                    ps = SchoolDataBase.getCon().prepareStatement("select Distinct STUDENTINFO.ID,STUDENTINFO.NAME,STUDENTINFO.Sex,Continues_Assessment,Exam,"
                            + "Total,Grade from STUDENTINFO "
                            + "Left Join SCORESHEET On STUDENTINFO.ID=SCORESHEET.ID "
                            + "where STUDENTINFO.CLASS=? and SCORESHEET.SUBJECT=? and Term=? and Academic_Year=?");
                    ps.setString(1, comboClass.getValue().toString());
                    ps.setString(2, newValue.toString());
                    ps.setString(3, curTermList.get(0).toString());
                    ps.setString(4, curTermList.get(1).toString());

                    rs = ps.executeQuery();
                    while (rs.next()) {
                        obList.add(new Score(rs.getInt("Id"), rs.getString("Name"), rs.getString("Sex"), rs.getInt("Continues_Assessment") + "", rs.getInt("Exam") + "",
                                rs.getInt("Total"), rs.getString("Grade")));
                    }
                    ps.close();
                    rs.close();

                    //Incase Gradeing System is changed
                    try {
                        ps = SchoolDataBase.getCon().prepareStatement("Select Score,Grade from GradingSystem");
                        rs = ps.executeQuery();
                        Score.sList.clear();
                        Score.gList.clear();
                        while (rs.next()) {
                            Score.sList.add(rs.getString("Score").substring(0, 2));
                            Score.gList.add(rs.getString("Grade"));

                            //replace "0-" with "0" because the Grade result set last value will be "0-",  
                            if (Score.sList.contains("0-")) {
                                Score.sList.remove("0-");
                                Score.sList.add("0");
                            }
                        }
                        ps.close();
                        rs.close();
                    } catch (Exception e) {
                    }

                    //Table will be Empty when the Selected Subject Value is not in Database
                    //So check if StudentID is = to null and just display StudentID,Name and Sex from Database into the table 
                    if (columnStudID.getCellData(0) == null) {
                        butSave.setDisable(false);
                        butUpdate.setDisable(true);
                        obList.clear();
                        ps = SchoolDataBase.getCon().prepareStatement("select ID,NAME,SEX from STUDENTINFO where CLASS=?");
                        ps.setString(1, comboClass.getValue().toString());
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            obList.add(new Score(rs.getInt("Id"), rs.getString("Name"), rs.getString("Sex"), "", "", 0, ""));
                        }
                        ps.close();
                        rs.close();
                    }
                } catch (Exception e) {
                }
            });

            //Automatically resize columns when table is resized
            /*columnStudID.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnStudName.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnSex.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnConAssess.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnEaxm.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnTotal.prefWidthProperty().bind(tableView.widthProperty().divide(7));
            columnGrade.prefWidthProperty().bind(tableView.widthProperty().divide(7));*/
        } catch (Exception e) {
        }
    }

    private class SaveTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            Platform.runLater(() -> {
                try {
                    if (comboClass.getValue() == null || comboSub.getValue() == null) {
                        SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Score Sheet", "Please select Class and Subject");
                    } else {
                        String classCombo = comboClass.getValue().toString();
                        String subCombo = comboSub.getValue().toString();

                        ps = SchoolDataBase.getCon().prepareStatement("Insert into ScoreSheet values(?,?,?,?,?,?,?,?,?,?,?)");
                        for (Object o : tableView.getItems()) {
                            ps.setInt(1, Integer.parseInt(columnStudID.getCellData(o).toString()));
                            ps.setString(2, columnStudName.getCellData(o).toString());
                            ps.setInt(3, Integer.parseInt(columnConAssess.getCellData(o).toString()));
                            ps.setInt(4, Integer.parseInt(columnEaxm.getCellData(o).toString()));
                            ps.setInt(5, Integer.parseInt(columnTotal.getCellData(o).toString()));
                            ps.setString(6, classCombo);
                            ps.setString(7, subCombo);
                            ps.setString(8, curTermList.get(0).toString());
                            ps.setString(9, curTermList.get(1).toString());
                            ps.setString(10, columnGrade.getCellData(o).toString());
                            ps.setString(11, columnSex.getCellData(o).toString());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        ps.close();

                        //Clear Academic Report Table First
                        ps = SchoolDataBase.getCon().prepareStatement("Truncate table AcademicReport");
                        ps.executeUpdate();
                        ps.close();

                        //Copy ID,Student Name,Class and Term values from Score Sheet and Paste into Academic Report
                        ps = SchoolDataBase.getCon().prepareStatement("Insert into AcademicReport(ID,Student_Name,Class,Term,Academic_Year) Select ID,"
                                + "Student_Name,Class,Term,Academic_Year From ScoreSheet");
                        ps.executeUpdate();
                        ps.close();

                        //First delete from Report if the Student report is already there
                        //This avoids duplicate when the same students are been scored but different subject
                        ps = SchoolDataBase.getCon().prepareStatement("DELETE FROM REPORT WHERE STUDENT_NAME=? and CLASS=? and TERM=? and ACADEMIC_YEAR=?");
                        for (Object o : tableView.getItems()) {
                            ps.setString(1, columnStudName.getCellData(o).toString());
                            ps.setString(2, classCombo);
                            ps.setString(3, curTermList.get(0).toString());
                            ps.setString(4, curTermList.get(1).toString());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        ps.close();

                        //Select form ScoreSheet and Insert Into Report
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT distinct STUDENT_NAME,Sex,sum(total) as Total, Avg(Cast(Total as decimal(10,1))) as Total_Avg,"
                                + " Class,Term,Academic_Year FROM SCORESHEET where STUDENT_NAME=? and Class=? and TERM=? AND ACADEMIC_YEAR=?"
                                + " GROUP BY STUDENT_NAME,SEX,Class,Term,Academic_Year");
                        for (Object o : tableView.getItems()) {
                            ps.setString(1, columnStudName.getCellData(o).toString());
                            ps.setString(2, classCombo);
                            ps.setString(3, curTermList.get(0).toString());
                            ps.setString(4, curTermList.get(1).toString());

                            rs = ps.executeQuery();
                            while (rs.next()) {
                                ps2 = SchoolDataBase.getCon().prepareStatement("Insert into Report (STUDENT_NAME,Sex,Total_Score,Average,Class,Term,Academic_Year) values(?,?,?,?,?,?,?)");
                                ps2.setString(1, rs.getString("STUDENT_NAME"));
                                ps2.setString(2, rs.getString("Sex"));
                                ps2.setInt(3, rs.getInt("Total"));
                                ps2.setDouble(4, Math.round(rs.getDouble("Total_Avg")));
                                ps2.setString(5, rs.getString("Class"));
                                ps2.setString(6, rs.getString("Term"));
                                ps2.setString(7, rs.getString("Academic_Year"));
                                ps2.executeUpdate();
                                ps2.close();
                            }

                            /*
                            Once Average value is Inserted Select all of them
                            And test if the value is > grade score list last value
                            if true. insert "P" and "Pass" to Fail_Pass and Remark column
                            else insert "F" and "Fail"
                             */
                            stm = SchoolDataBase.getCon().createStatement();
                            stm.executeQuery("Select Average from Report");
                            rs = stm.getResultSet();
                            while (rs.next()) {
                                ps2 = SchoolDataBase.getCon().prepareStatement("Update Report set Fail_Pass=?,Remark=? Where Student_Name=? and Class=? and TERM=? AND ACADEMIC_YEAR=?");
                                //sList.get(sList.size()-1) give the last value on the list (the lowwest score)
                                if (rs.getDouble("Average") > Integer.parseInt(sList.get(sList.size() - 1).toString())) {
                                    ps2.setString(1, "P");
                                    ps2.setString(2, "Pass");
                                    ps2.setString(3, columnStudName.getCellData(o).toString());
                                    ps2.setString(4, classCombo);
                                    ps2.setString(5, curTermList.get(0).toString());
                                    ps2.setString(6, curTermList.get(1).toString());
                                    ps2.executeUpdate();
                                } else {
                                    ps2.setString(1, "F");
                                    ps2.setString(2, "Fail");
                                    ps2.setString(3, columnStudName.getCellData(o).toString());
                                    ps2.setString(4, classCombo);
                                    ps2.setString(5, curTermList.get(0).toString());
                                    ps2.setString(6, curTermList.get(1).toString());
                                    ps2.executeUpdate();
                                }
                            }
                        }

                        SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Score Sheet", "Saved");
                    }
                } catch (Exception e) {
                }
                butSave.setDisable(true);
                butUpdate.setDisable(false);
            });

            return null;
        }

    }

    @FXML
    private void saveAction() {
        vBox.setDisable(true);
        progress.setVisible(true);

        SaveTask task = new SaveTask();
        task.setOnSucceeded((event) -> {
            vBox.setDisable(false);
            progress.setVisible(false);
        });
        new Thread(task).start();
    }

    private class UpdateTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            Platform.runLater(() -> {

                try {
                    if (comboClass.getValue() == null || comboSub.getValue() == null) {
                        SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Score Sheet", "Please select Class and Subject");
                    } else {
                        String classCombo = comboClass.getValue().toString();
                        String subCombo = comboSub.getValue().toString();

                        ps = SchoolDataBase.getCon().prepareStatement("UPDATE SCORESHEET set CONTINUES_ASSESSMENT=?,EXAM=?,TOTAL=?,GRADE=?"
                                + "where Student_Name=? and CLASS=? and SUBJECT=? and TERM=? and ACADEMIC_YEAR=?");
                        for (Object o : tableView.getItems()) {
                            ps.setInt(1, Integer.parseInt(columnConAssess.getCellData(o).toString()));
                            ps.setInt(2, Integer.parseInt(columnEaxm.getCellData(o).toString()));
                            ps.setInt(3, Integer.parseInt(columnTotal.getCellData(o).toString()));
                            ps.setString(4, columnGrade.getCellData(o).toString());
                            ps.setString(5, columnStudName.getCellData(o).toString());
                            ps.setString(6, classCombo);
                            ps.setString(7, subCombo);
                            ps.setString(8, curTermList.get(0).toString());
                            ps.setString(9, curTermList.get(1).toString());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        ps.close();

                        //Clear Academic Report Table First
                        ps = SchoolDataBase.getCon().prepareStatement("Truncate table AcademicReport");
                        ps.executeUpdate();
                        ps.close();

                        //Copy ID,Student Name,Class and Term values from Score Sheet and Paste into Academic Report
                        ps = SchoolDataBase.getCon().prepareStatement("Insert into AcademicReport(ID,Student_Name,Class,Term,Academic_Year) Select ID,"
                                + "Student_Name,Class,Term,Academic_Year From ScoreSheet");
                        ps.executeUpdate();
                        ps.close();

                        //Select form ScoreSheet and Update Into Report
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT sum(total) as Total, Avg(Cast(Total as decimal(10,1))) as Total_Avg"
                                + " FROM SCORESHEET where STUDENT_NAME=? and Class=? and TERM=? AND ACADEMIC_YEAR=?");
                        for (Object o : tableView.getItems()) {
                            ps.setString(1, columnStudName.getCellData(o).toString());
                            ps.setString(2, classCombo);
                            ps.setString(3, curTermList.get(0).toString());
                            ps.setString(4, curTermList.get(1).toString());

                            rs = ps.executeQuery();
                            while (rs.next()) {
                                ps2 = SchoolDataBase.getCon().prepareStatement("UPDATE Report set Total_Score=?,Average=?"
                                        + " WHERE STUDENT_NAME=? and Class=? and TERM=? AND ACADEMIC_YEAR=?");
                                ps2.setInt(1, rs.getInt("Total"));
                                ps2.setDouble(2, Math.round(rs.getDouble("Total_Avg")));
                                ps2.setString(3, columnStudName.getCellData(o).toString());
                                ps2.setString(4, classCombo);
                                ps2.setString(5, curTermList.get(0).toString());
                                ps2.setString(6, curTermList.get(1).toString());
                                ps2.executeUpdate();
                                ps2.close();
                            }

                            /*
                            Once Average value is Updated Select all of them
                            And test if the value is > grade score list last value
                            if true. insert "P" and "Pass" to Fail_Pass and Remark column
                            else insert "F" and "Fail"
                             */
                            stm = SchoolDataBase.getCon().createStatement();
                            stm.executeQuery("Select Average from Report");
                            rs = stm.getResultSet();
                            while (rs.next()) {
                                ps2 = SchoolDataBase.getCon().prepareStatement("Update Report set Fail_Pass=?,Remark=? Where Student_Name=? and Class=? and TERM=? AND ACADEMIC_YEAR=?");
                                if (rs.getDouble("Average") > Integer.parseInt(sList.get(sList.size() - 1).toString())) {
                                    ps2.setString(1, "P");
                                    ps2.setString(2, "Pass");
                                    ps2.setString(3, columnStudName.getCellData(o).toString());
                                    ps2.setString(4, classCombo);
                                    ps2.setString(5, curTermList.get(0).toString());
                                    ps2.setString(6, curTermList.get(1).toString());
                                    ps2.executeUpdate();
                                } else {
                                    ps2.setString(1, "F");
                                    ps2.setString(2, "Fail");
                                    ps2.setString(3, columnStudName.getCellData(o).toString());
                                    ps2.setString(4, classCombo);
                                    ps2.setString(5, curTermList.get(0).toString());
                                    ps2.setString(6, curTermList.get(1).toString());
                                    ps2.executeUpdate();
                                }
                            }
                        }

                        schoolalertdialog.SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Score Sheet", "Updated");
                    }
                } catch (Exception e) {
                }
            });

            return null;
        }

    }

    @FXML
    private void updateAction() {
        vBox.setDisable(true);
        progress.setVisible(true);

        UpdateTask task = new UpdateTask();
        task.setOnSucceeded((event) -> {
            vBox.setDisable(false);
            progress.setVisible(false);
        });
        new Thread(task).start();
    }
}
