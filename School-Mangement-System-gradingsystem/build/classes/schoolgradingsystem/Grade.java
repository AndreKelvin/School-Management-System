/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgradingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class Grade {
    
    private StringProperty score;
    private StringProperty grade;
    private StringProperty remark;

    public Grade(String score, String grade, String remark) {
        this.score = new SimpleStringProperty(score);
        this.grade = new SimpleStringProperty(grade);
        this.remark = new SimpleStringProperty(remark);
    }

    @Override
    public String toString() {
        return score.get();
    }

    public String getScore() {
        return score.get();
    }

    public String getGrade() {
        return grade.get();
    }

    public String getRemark() {
        return remark.get();
    }

    public void setScore(String score) {
        this.score.set(score);
    }

    public void setGrade(String grade) {
        this.grade.set(grade);
    }

    public void setRemark(String remark) {
        this.remark.set(remark);
    }
    
    public StringProperty scoreProperty(){
        return score;
    }
    
    public StringProperty gradeProperty(){
        return grade;
    }
    
    public StringProperty remarkProperty(){
        return remark;
    }
}
