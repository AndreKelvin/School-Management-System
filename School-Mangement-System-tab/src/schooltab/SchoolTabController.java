/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooltab;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolTabController {

    @FXML
    private BorderPane bPaneClass, bPaneSub, bPaneTerm, bPaneGrade, bPaneTeach, bPaneFees, bPaneSchDetails, bPaneStuFee, bPaneSalary, bPaneExpenses;
    public JFXButton but;
    private Parent rootSub, rootClass, rootTerm, rootGrade, rootTeach, rootFess, rootDetails, rootStudentFee, rootSalary, rootExpenses;

    public void setRootSub(Parent rootSub) {
        this.rootSub = rootSub;
    }

    public void setRootClass(Parent rootClass) {
        this.rootClass = rootClass;
    }

    public void setRootTerm(Parent rootTerm) {
        this.rootTerm = rootTerm;
    }

    public void setRootGrade(Parent rootGrade) {
        this.rootGrade = rootGrade;
    }

    public void setRootTeach(Parent rootTeach) {
        this.rootTeach = rootTeach;
    }

    public void setRootFess(Parent rootFess) {
        this.rootFess = rootFess;
    }

    public void setRootDetails(Parent rootDetails) {
        this.rootDetails = rootDetails;
    }

    public void setRootStudentFee(Parent rootStudentFee) {
        this.rootStudentFee = rootStudentFee;
    }

    public void setRootSalary(Parent rootSalary) {
        this.rootSalary = rootSalary;
    }

    public void setRootExpenses(Parent rootExpenses) {
        this.rootExpenses = rootExpenses;
    }
    

    @FXML
    public void classAction() {
        bPaneClass.setCenter(rootClass);
    }

    @FXML
    private void subjectAction() {
        bPaneSub.setCenter(rootSub);
    }

    @FXML
    private void termAction() {
        bPaneTerm.setCenter(rootTerm);
    }

    @FXML
    private void gradeAction() {
        bPaneGrade.setCenter(rootGrade);
    }

    @FXML
    private void teacherAction() {
        bPaneTeach.setCenter(rootTeach);
    }

    @FXML
    private void schoolFeesAction() {
        bPaneFees.setCenter(rootFess);
    }

    @FXML
    private void schoolDetailsAction() {
        bPaneSchDetails.setCenter(rootDetails);
    }

    @FXML
    private void openStudentFees() {
        bPaneStuFee.setCenter(rootStudentFee);
    }

    @FXML
    private void openSalary() {
        bPaneSalary.setCenter(rootSalary);
    }

    @FXML
    private void openSchoolExpenses() {
        bPaneExpenses.setCenter(rootExpenses);
    }
}
