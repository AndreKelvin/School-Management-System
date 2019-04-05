/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolterm;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class TermTable {
    
    private StringProperty name;
    private StringProperty start;
    private StringProperty end;
    private StringProperty acaYear;
    
    public TermTable(String name, String start, String end, String acaYear){
        this.name=new SimpleStringProperty(name);
        this.start=new SimpleStringProperty(start);
        this.end=new SimpleStringProperty(end);
        this.acaYear=new SimpleStringProperty(acaYear);
    }
    
    public TermTable(){
        
    }

    public String getName(){
        return name.get();
    }
    
    public String getStart(){
        return start.get();
    }
    
    public String getEnd(){
        return end.get();
    }
    
    public String getAcaYear(){
        return acaYear.get();
    }
    
    public void setName(String name) {
        this.name.set(name);
    }
    
    public void setStart(String start) {
        this.start.set(start);
    }
    
    public void setEnd(String end) {
        this.end.set(end);
    }
    
    public void setAcaYear(String acaYear) {
        this.acaYear.set(acaYear);
    }
    
    public StringProperty nameProperty(){
        return name;
    }
    
    public StringProperty startProperty(){
        return start;
    }
    
    public StringProperty endProperty(){
        return end;
    }
    
    public StringProperty acaYearProperty(){
        return acaYear;
    }
}
