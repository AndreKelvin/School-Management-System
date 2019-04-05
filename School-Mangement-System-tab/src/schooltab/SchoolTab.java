/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooltab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schooldailyexpenses.SchoolDailyExpenses;
import schooldetails.SchoolDetails;
import schoolfees.SchoolFees;
import schoolgradingsystem.SchoolGradingSystem;
import schoolsalary.SchoolSalary;
import schoolstudentclass.SchoolStudentClass;
import schoolstudentfees.SchoolStudentFees;
import schoolstudentsubject.SchoolStudentSubject;
import schoolteacher.SchoolTeacher;
import schoolterm.SchoolTerm;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolTab extends Application {
    
    private Parent root;
    private SchoolStudentClass stdClass;
    private SchoolStudentSubject stdSub;
    private SchoolTerm stdTerm;
    private SchoolGradingSystem stdGrade;
    private SchoolTeacher schTeach;
    private SchoolFees schFees;
    private SchoolDetails schDetails;
    private SchoolStudentFees stdFees;
    private SchoolSalary salary;
    private SchoolDailyExpenses expenses;
    private FXMLLoader loader;
    
    @Override
    public void start(Stage stage) throws Exception {
        //root = FXMLLoader.load(getClass().getResource("SchoolTab.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    public SchoolTab() {
        try {
            stdSub=new SchoolStudentSubject();
            stdFees=new SchoolStudentFees();
            stdGrade=new SchoolGradingSystem();
            schTeach=new SchoolTeacher();
            schFees=new SchoolFees();
            schDetails=new SchoolDetails();
            stdClass=new SchoolStudentClass();
            stdTerm=new SchoolTerm();
            salary=new SchoolSalary();
            expenses=new SchoolDailyExpenses();
            
            loader = new FXMLLoader(getClass().getResource("SchoolTab.fxml"));
            root=loader.load();
            
            SchoolTabController tab=loader.getController();
            
            tab.setRootSub(stdSub.getFxml());
            tab.setRootClass(stdClass.getFxml());
            tab.setRootTerm(stdTerm.getFxml());
            tab.setRootGrade(stdGrade.getFxml());
            tab.setRootTeach(schTeach.getFxml());
            tab.setRootFess(schFees.getFxml());
            tab.setRootDetails(schDetails.getFxml());
            tab.setRootStudentFee(stdFees.getFxml());
            tab.setRootSalary(salary.getFxml());
            tab.setRootExpenses(expenses.getFxml());
            
            tab.classAction();
        } catch (Exception e) {}
    }
    
     public Parent getFxml() {
        return root;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
