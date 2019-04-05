/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentfees;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class Fee {

    private StringProperty name;
    private StringProperty paidUnpaid;
    private StringProperty amount;
    private StringProperty balance;
    private String selectedClass;
    
    public Fee(String name, String paidUnpaid, String amount, String balance, String selectedClass) {
        this.name = new SimpleStringProperty(name);
        this.paidUnpaid = new SimpleStringProperty(paidUnpaid);
        this.amount = new SimpleStringProperty(amount);
        this.balance = new SimpleStringProperty(balance);
        this.selectedClass = selectedClass;

        /*
        Listen for changes in Amount Column
        Which is going to get the inputed Amount Value
        And substract it with the School Fee Amount of the selected Class
        Then display the total Balance remining in Balance Column
         */
        this.amount.addListener((observable, oldValue, newValue) -> {
            try {
                int balanceFee = 0;

                PreparedStatement ps = SchoolDataBase.getCon().prepareStatement("Select Fee From SchoolFee Where Class=?");
                ps.setString(1, selectedClass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    balanceFee = Integer.parseInt(rs.getString("Fee")) - Integer.parseInt(this.amount.get());
                }
                this.balance.set(balanceFee + "");

                //Input UnPaid if the Amount Value is lesser than or equal to 0
                //else input Paid
                if (Integer.parseInt(this.amount.get())==Integer.parseInt(rs.getString("Fee"))) {
                    this.paidUnpaid.set("Fully Paid");
                } else if(Integer.parseInt(this.amount.get())>0){
                    this.paidUnpaid.set("Paid");
                } else {
                    this.paidUnpaid.set("UnPaid");
                }
            } catch (Exception e) {
            }
        });
    }

    public String getName() {
        return name.get();
    }

    public String getPaidUnpaid() {
        return paidUnpaid.get();
    }

    public String getAmount() {
        return amount.get();
    }

    public String getBalance() {
        return balance.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPaidUnpaid(String paidUnpaid) {
        this.paidUnpaid.set(paidUnpaid);
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public void setBalance(String balance) {
        this.balance.set(balance);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty paidUnpaidProperty() {
        return paidUnpaid;
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public StringProperty balanceProperty() {
        return balance;
    }

}
