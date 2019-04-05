/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolfees;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class Fee {
    
    private StringProperty clas;
    private StringProperty fee;

    public Fee(String clas, String fee) {
        this.clas = new SimpleStringProperty(clas);
        this.fee = new SimpleStringProperty(fee);
    }

    public String getClas() {
        return clas.get();
    }

    public String getFee() {
        return fee.get();
    }
    
    public void setClas(String clas) {
        this.clas.set(clas);
    }
    
    public void setFee(String fee) {
        this.fee.set(fee);
    }
    
    public StringProperty clasProperty(){
        return clas;
    }
    
    public StringProperty feeProperty(){
        return fee;
    }
}
