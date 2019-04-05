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
public class AcademicTable {
    
    private String clas,term,acaYear,remark;

    public AcademicTable(String clas, String term, String acaYear,String remark) {
        this.clas = clas;
        this.term = term;
        this.acaYear=acaYear;
        this.remark = remark;
    }

    public String getClas() {
        return clas;
    }

    public String getTerm() {
        return term;
    }
    
    public String getAcaYear() {
        return acaYear;
    }

    public String getRemark() {
        return remark;
    }
    
    
}
