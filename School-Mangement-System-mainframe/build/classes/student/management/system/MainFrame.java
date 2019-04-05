/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student.management.system;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;
import schoolreporttab.SchoolReportTab;
import schoolscoresheet.SchoolScoreSheet;
import schoolstudentdetails.SchoolStudentDetails;
import schooltab.SchoolTab;

/**
 *
 * @author Andre Kelvin
 */
public class MainFrame extends Application {

    private Connection con;
    private SchoolScoreSheet scoreSheet;
    private SchoolStudentDetails stdInfo;
    private SchoolTab stdTab;
    private SchoolReportTab stdReTab;
    private Parent root;

    @Override
    public void init() {
        try {
            Statement stm;
            File file = new File(System.getProperty("user.home") + File.separator + "SchoolDB");
            
            if (!file.exists()) {
                file.mkdir();

                System.setProperty("derby.system.home", System.getProperty("user.home") + File.separator + "SchoolDB");
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

                con = DriverManager.getConnection("jdbc:derby:mydatabase;create=true;");
                stm = con.createStatement();
                stm.executeUpdate("create table STUDENTINFO ("
                        + "ID INTEGER Generated Always as Identity (start with 1, increment by 1) not null primary key,"
                        + "NAME VARCHAR(100) not null,"
                        + "SEX CHAR(1) not null,"
                        + "AGE DATE,"
                        + "PHOTO VARCHAR(255),"
                        + "CLASS VARCHAR(10),"
                        + "PARENT VARCHAR(100),"
                        + "ADDRESS VARCHAR(255),"
                        + "NUMBER VARCHAR(20)" + ")");

                stm.executeUpdate("create table ACADEMICREPORT ("
                        + "ID INTEGER not null,"
                        + "STUDENT_NAME VARCHAR(100) not null,"
                        + "CLASS VARCHAR(12) not null,"
                        + "TERM VARCHAR(17) not null,"
                        + "ACADEMIC_YEAR VARCHAR(10) not null,"
                        + "REMARK VARCHAR(12)" + ")");

                stm.executeUpdate("create table CURRENTTERM ("
                        + "TERM VARCHAR(12) not null,"
                        + "ACADEMIC_YEAR VARCHAR(10) not null" + ")");

                stm.executeUpdate("create table GRADINGSYSTEM ("
                        + "SCORE VARCHAR(12),"
                        + "GRADE VARCHAR(2),"
                        + "REMARK VARCHAR(12)" + ")");

                stm.executeUpdate("create table REPORT("
                        + "STUDENT_NAME VARCHAR(100) not null,"
                        + "SEX CHAR(1) not null,"
                        + "TOTAL_SCORE INTEGER not null,"
                        + "AVERAGE DOUBLE not null,"
                        + "POSITION VARCHAR(6),"
                        + "FAIL_PASS VARCHAR(4),"
                        + "REMARK VARCHAR(15),"
                        + "CLASS VARCHAR(10),"
                        + "TERM VARCHAR(12),"
                        + "ACADEMIC_YEAR VARCHAR(10)" + ")");

                stm.executeUpdate("create table SCHOOLFEE ("
                        + "CLASS VARCHAR(15) not null,"
                        + "FEE VARCHAR(20) not null" + ")");

                stm.executeUpdate("create table SCHOOL_DETAILS ("
                        + "LOGO VARCHAR(255) not null,"
                        + "NAME VARCHAR(255) not null" + ")");

                stm.executeUpdate("create table SCORESHEET ("
                        + "ID INTEGER not null,"
                        + "STUDENT_NAME VARCHAR(100) not null,"
                        + "CONTINUES_ASSESSMENT INTEGER,"
                        + "EXAM INTEGER,"
                        + "TOTAL INTEGER,"
                        + "CLASS VARCHAR(10) not null,"
                        + "SUBJECT VARCHAR(100) not null,"
                        + "TERM VARCHAR(12) not null,"
                        + "ACADEMIC_YEAR VARCHAR(10) not null,"
                        + "GRADE VARCHAR(2),"
                        + "SEX CHAR(1)" + ")");

                stm.executeUpdate("create table STUDENTCLASS ("
                        + "CLASS VARCHAR(10) not null" + ")");

                stm.executeUpdate("create table STUDENTSUBJECT ("
                        + "ID INTEGER Generated Always as Identity (start with 1, increment by 1) not null,"
                        + "SUBJECT VARCHAR(50) not null,"
                        + "CLASS VARCHAR(10) not null" + ")");

                stm.executeUpdate("create table STUDENTTERM ("
                        + "NAME VARCHAR(12) not null,"
                        + "STARTING DATE not null,"
                        + "ENDING DATE not null,"
                        + "ACADEMIC_YEAR VARCHAR(10) not null" + ")");

                stm.executeUpdate("create table SUMMARY ("
                        + "TOTAL_STUDENT INTEGER not null,"
                        + "TOTAL_PASS INTEGER not null,"
                        + "TOTAL_FAIL INTEGER not null,"
                        + "TOTAL_BOYS INTEGER not null,"
                        + "BOYS_PASS INTEGER not null,"
                        + "BOYS_FAIL INTEGER not null,"
                        + "TOTAL_GIRLS INTEGER not null,"
                        + "GIRLS_PASS INTEGER not null,"
                        + "GIRLS_FAIL INTEGER not null,"
                        + "CLASS VARCHAR(15),"
                        + "TERM VARCHAR(15),"
                        + "ACADEMIC_YEAR VARCHAR(10)" + ")");

                stm.executeUpdate("create table TEACHER ("
                        + "NAME VARCHAR(100) not null,"
                        + "DATE_OF_BIRTH DATE not null,"
                        + "GENDER CHAR(1) not null,"
                        + "QUALIFICATION VARCHAR(100) not null,"
                        + "SALARY VARCHAR(10) not null,"
                        + "DATE_JOINED VARCHAR(10) not null,"
                        + "MARITAL_STATUS VARCHAR(12) not null,"
                        + "CHILDREN SMALLINT not null,"
                        + "SPOUSE VARCHAR(100),"
                        + "NATIONALITY VARCHAR(50) not null,"
                        + "NUMBER VARCHAR(30) not null,"
                        + "EMAIL VARCHAR(255),"
                        + "ADDRESS VARCHAR(255) not null,"
                        + "CLASS VARCHAR(12) not null,"
                        + "SUBJECT VARCHAR(100) not null,"
                        + "PHOTO VARCHAR(255) not null" + ")");

                stm.executeUpdate("create table STUDENT_FEE ("
                        + "CLASS VARCHAR(10) not null,"
                        + "NAME VARCHAR(255) not null,"
                        + "PAID_UNPAID VARCHAR(13) not null,"
                        + "TERM VARCHAR(12) not null,"
                        + "ACADEMIC_YEAR VARCHAR(10) not null,"
                        + "AMOUNT INTEGER not null,"
                        + "BALANCE INTEGER not null" + ")");

                stm.executeUpdate("create table SALARY ("
                        + "DATE DATE not null,"
                        + "TEACHER VARCHAR(255) not null,"
                        + "SALARY VARCHAR(30) not null,"
                        + "PAIDUNPAID VARCHAR(30) not null,"
                        + "AMOUNT INTEGER not null" + ")");

                stm.executeUpdate("create table DAILY_EXPENSES ("
                        + "DATE DATE not null,"
                        + "DESCRIPTION VARCHAR(255) not null,"
                        + "AMOUNT INTEGER not null" + ")");

                SchoolDataBase.initDB();

                PreparedStatement ps = SchoolDataBase.getCon().prepareStatement("Insert Into CurrentTerm Values(?,?)");
                ps.setString(1, "");
                ps.setString(2, "");
                ps.executeUpdate();
            }
            
            /*
            The Previous version doesn't have STUDENT_FEE,SALARY,DAILY_EXPENSES
            Database Tables So the below code checks if the Tables Exit
            And Create them if Tables doesn't Exit
            */
//            SchoolDataBase.initDB();
//            stm = SchoolDataBase.getCon().createStatement();
//            DatabaseMetaData dbm=SchoolDataBase.getCon().getMetaData();
//            ResultSet rs=dbm.getTables(null, null, "SALARY", null);
//            if (rs.next()) {
//                
//            }else{
//                stm.executeUpdate("create table STUDENT_FEE ("
//                        + "CLASS VARCHAR(10) not null,"
//                        + "NAME VARCHAR(255) not null,"
//                        + "PAID_UNPAID VARCHAR(13) not null,"
//                        + "TERM VARCHAR(12) not null,"
//                        + "ACADEMIC_YEAR VARCHAR(10) not null,"
//                        + "AMOUNT INTEGER not null,"
//                        + "BALANCE INTEGER not null" + ")");
//
//                stm.executeUpdate("create table SALARY ("
//                        + "DATE DATE not null,"
//                        + "TEACHER VARCHAR(255) not null,"
//                        + "SALARY VARCHAR(30) not null,"
//                        + "PAIDUNPAID VARCHAR(30) not null,"
//                        + "AMOUNT INTEGER not null" + ")");
//
//                stm.executeUpdate("create table DAILY_EXPENSES ("
//                        + "DATE DATE not null,"
//                        + "DESCRIPTION VARCHAR(255) not null,"
//                        + "AMOUNT INTEGER not null" + ")");
//            }
            
            stdInfo = new SchoolStudentDetails();
            scoreSheet = new SchoolScoreSheet();
            stdReTab = new SchoolReportTab();
            stdTab = new SchoolTab();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFrame.fxml"));
            root = loader.load();
            MainFrameController main = loader.getController();
            main.getRoots(stdInfo.getFxml(), scoreSheet.getFxml(), stdReTab.getFxml(), stdTab.getFxml());
        } catch (Exception e) {
        }
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException, ExecutionException {

        Scene scene = new Scene(root, 1350, 700);

        stage.setTitle("School Management System");

        stage.setOnCloseRequest((WindowEvent event) -> {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.CONFIRMATION, "Eixt", "Are you sure you want to exit?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");

            //Remove the current Buttons and Add "Yes" "No" Buttons to Alert Dialog
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes, no);

            Optional<ButtonType> op = SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                SchoolDataBase.closeDB();
                stage.close();
            } else {
                event.consume();
            }
        });

        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("schoolhouse-icon.png")));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
