/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolreport;

/**
 *
 * @author Andre Kelvin
 */
public class Report {
    
    private String name;
    private String sex;
    private short tScore;
    private float avg;
    private String pos;
    private String passFail;
    private String remark;

    public Report(String name, String sex, short tScore, float avg, String pos, String passFail, String remark) {
        this.name = name;
        this.sex = sex;
        this.tScore = tScore;
        this.avg = avg;
        this.pos = pos;
        this.passFail = passFail;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public short getTScore() {
        return tScore;
    }

    public float getAvg() {
        return avg;
    }

    public String getPos() {
        return pos;
    }

    public String getPassFail() {
        return passFail;
    }

    public String getRemark() {
        return remark;
    }
    
     
}
