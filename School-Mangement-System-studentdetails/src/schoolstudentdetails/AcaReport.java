/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentdetails;

/**
 *
 * @author Andre Kelvin
 */
public class AcaReport {
    private String sub;
    private int conAss;
    private int exam;
    private int total;
    private String grade;

    public AcaReport(String sub, int conAss, int exam, int total, String grade) {
        this.sub = sub;
        this.conAss = conAss;
        this.exam = exam;
        this.total = total;
        this.grade = grade;
    }

    public String getSub() {
        return sub;
    }

    public int getConAss() {
        return conAss;
    }

    public int getExam() {
        return exam;
    }

    public int getTotal() {
        return total;
    }

    public String getGrade() {
        return grade;
    }
    
    
}
