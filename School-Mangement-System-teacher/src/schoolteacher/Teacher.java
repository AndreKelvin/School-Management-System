/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolteacher;

/**
 *
 * @author Andre Kelvin
 */
public class Teacher {
    
    private String subject,clas;

    public Teacher(String subject, String clas) {
        this.subject = subject;
        this.clas = clas;
    }

    public String getSubject() {
        return subject;
    }

    public String getClas() {
        return clas;
    }
}
