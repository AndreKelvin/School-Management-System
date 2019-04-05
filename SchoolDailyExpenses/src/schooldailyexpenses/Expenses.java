/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldailyexpenses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class Expenses {
    
    private StringProperty description;
    private IntegerProperty amount;

    public Expenses(String description, int amount) {
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleIntegerProperty(amount);
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public int getAmount() {
        return amount.get();
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public StringProperty descriptionProperty() {
        return description;
    }
    
    public IntegerProperty amountProperty() {
        return amount;
    }
    
}
