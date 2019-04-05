/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolsalary;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class Salary {

    private StringProperty teacher;
    private StringProperty salary;
    private StringProperty paidUnpaid;
    private StringProperty amount;

    public Salary(String teacher, String salary, String paidUnpaid, String amount) {
        this.teacher = new SimpleStringProperty(teacher);
        this.salary = new SimpleStringProperty(salary);
        this.paidUnpaid = new SimpleStringProperty(paidUnpaid);
        this.amount = new SimpleStringProperty(amount);

        /*
        Listen for changes in Amount Column
        Input UnPaid if the Amount Value is lesser than or equal to 0
        else input Paid
        */
        this.amount.addListener((observable, oldValue, newValue) -> {
            int balance=Integer.parseInt(this.salary.get().replace(",", ""))-Integer.parseInt(this.amount.get());
            
            if (Integer.parseInt(this.amount.get()) <= 0) {
                this.paidUnpaid.set("Unpaid");
            } else if(balance!=0) {
                this.paidUnpaid.set(this.amount.get()+" Paid /"+ balance+" Unpaid");
            } else {
                this.paidUnpaid.set("Paid");
            }
        });
    }

    public String getTeacher() {
        return teacher.get();
    }

    public String getSalary() {
        return salary.get();
    }

    public String getPaidUnpaid() {
        return paidUnpaid.get();
    }
    
    public String getAmount() {
        return amount.get();
    }

    public void setTeacher(String teacher) {
        this.teacher.set(teacher);
    }

    public void setSalary(String salary) {
        this.salary.set(salary);
    }
    
    public void setPaidUnpaid(String paidUnpaid) {
        this.paidUnpaid.set(paidUnpaid);
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public StringProperty teacherProperty() {
        return teacher;
    }

    public StringProperty salaryProperty() {
        return salary;
    }

    public StringProperty paidUnpaidProperty() {
        return paidUnpaid;
    }
    
    public StringProperty amountProperty() {
        return amount;
    }

}
