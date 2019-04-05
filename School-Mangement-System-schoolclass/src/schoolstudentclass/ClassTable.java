/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentclass;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andre Kelvin
 */
public class ClassTable {
    
    private StringProperty className;

    public ClassTable(String className) {
        this.className = new SimpleStringProperty(className);
    }

    @Override
    public String toString() {
        return className.get();
    }

    public String getClassName() {
        return className.get();
    }

    public void setClassName(String className) {
        this.className.set(className);
    }
    
    public StringProperty classNameProperty(){
        return className;
    }
    
}
