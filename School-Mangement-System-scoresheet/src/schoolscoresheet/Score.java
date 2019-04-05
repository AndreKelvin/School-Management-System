/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolscoresheet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Andre Kelvin
 */
public class Score {
    
    private IntegerProperty studentID;
    private StringProperty studnetName;
    private StringProperty sex;
    private StringProperty continuesAssessment;
    private StringProperty exam;
    private IntegerProperty total;
    private StringProperty grade;
    public static List sList,gList;

    public Score() {
    }
    

    public Score(int studentID, String studnetName, String sex, String continuesAssessment, String exam, int total, String grade) {
        this.studentID = new SimpleIntegerProperty(studentID);
        this.studnetName = new SimpleStringProperty(studnetName);
        this.sex= new SimpleStringProperty(sex);
        this.continuesAssessment = new SimpleStringProperty(continuesAssessment);
        this.exam = new SimpleStringProperty(exam);
        this.total = new SimpleIntegerProperty(total);
        this.grade = new SimpleStringProperty(grade);
        
        try {
            sList=new ArrayList();
            gList=new ArrayList();
            
            PreparedStatement ps=schooldatabase.SchoolDataBase.getCon().prepareStatement("Select Score,Grade from GradingSystem");
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                sList.add(rs.getString("Score").substring(0, 2));
                gList.add(rs.getString("Grade"));
                
                //replace "0-" with "0" because the Grade result set last value will be "0-",  
                if (sList.contains("0-")) {
                    sList.remove("0-");
                    sList.add("0");
                }
            }
        } catch (Exception e) {}
        
        //To listen for changes in the Text Fields and input values in Total and Grade Columns
        ChangeListener change=(ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            short con,ex;
            try {
                con=Short.parseShort(this.continuesAssessment.get());
            } catch (Exception e) {
                con=0;
            }
            try {
                ex=Short.parseShort(this.exam.get());
            } catch (Exception e) {
                ex=0;
            }

            this.total.setValue(con+ex);
            
            for (int i = 0; i < sList.size(); i++) {
                if (this.total.get()>=Integer.parseInt(sList.get(i).toString())) {
                    this.grade.setValue(gList.get(i).toString());
                    break;
                }
            }
        };
        
        this.continuesAssessment.addListener(change);
        this.exam.addListener(change);
    }
    
    

    public int getStudentID() {
        return studentID.get();
    }

    public String getStudnetName() {
        return studnetName.get();
    }
    
    public String getSex() {
        return sex.get();
    }

    public String getContinuesAssessment() {
        return continuesAssessment.get();
    }

    public String getExam() {
        return exam.get();
    }

    public int getTotal() {
        return total.get();
    }

    public String getGrade() {
        return grade.get();
    }

    public void setStudentID(int StudentID) {
        this.studentID.set(StudentID);
    }

    public void setStudnetName(String studnetName) {
        this.studnetName.set(studnetName);
    }
    
    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public void setContinuesAssessment(String continuesAssessment) {
        this.continuesAssessment.set(continuesAssessment);
    }

    public void setExam(String exam) {
        this.exam.set(exam);
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public void setGrade(String grade) {
        this.grade.set(grade);
    }

    public IntegerProperty studentIDProperty() {
        return studentID;
    }

    public StringProperty studnetNameProperty() {
        return studnetName;
    }
    
    public StringProperty sexProperty() {
        return sex;
    }

    public StringProperty continuesAssessmentProperty() {
        return continuesAssessment;
    }
    
    public StringProperty examProperty() {
        return exam;
    }
    
    public IntegerProperty totalProperty() {
        return total;
    }

    public StringProperty gradeProperty() {
        return grade;
    }
}
