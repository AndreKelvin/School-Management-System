/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student.management.system;

import bridge.Bridge;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javax.imageio.ImageIO;
import schooldatabase.SchoolDataBase;
import schoolreporttab.SchoolReportTab;
import schoolscoresheet.SchoolScoreSheet;
import schoolstudentdetails.SchoolStudentDetails;
import schooltab.SchoolTab;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class MainFrameController implements Initializable {

    @FXML
    private BorderPane bPane, mainPane;
    @FXML
    private Label labelTerm, labelDate, labelStu, labelBoys, labelGirl, labelTeach, labelSchName;
    @FXML
    private ImageView imageView;
    private Parent rootScoreSheet, rootStdInfo, rootStdTab, rootStdReTab;
    private PreparedStatement ps;
    private ResultSet rs;

    public void getRoots(Parent root1, Parent root2, Parent root3, Parent root4) {
        rootStdInfo = root1;
        rootScoreSheet = root2;
        rootStdReTab = root3;
        rootStdTab = root4;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            System.out.println("Running Background Task...");
            //Display Current Term
            ps = SchoolDataBase.getCon().prepareStatement("Select * from CurrentTerm");
            rs = ps.executeQuery();
            if (rs.next()) {
                labelTerm.setText(rs.getString("Term") + " " + rs.getString("Academic_Year"));
            }
            ps.close();

            //Display School Logo and Name
            ps = SchoolDataBase.getCon().prepareStatement("Select * from School_Details");
            rs = ps.executeQuery();
            if (rs.next()) {
                File file = new File(rs.getString("Logo"));
                BufferedImage read = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(read, null);
                imageView.setImage(image);

                labelSchName.setText(rs.getString("Name").toUpperCase());
            }
            ps.close();

            //Display Total Teachers
            short num = 0;
            ps = SchoolDataBase.getCon().prepareStatement("SELECT Distinct Name FROM TEACHER");
            rs = ps.executeQuery();
            while (rs.next()) {
                num++;
            }
            labelTeach.setText(num + "");
            ps.close();

            //Display Date
            SimpleDateFormat date = new SimpleDateFormat("d/MM/Y");
            labelDate.setText(date.format(new Date()));

            /*
                    Pass Labels and Image View object so that any changes should also occur here
                    This is act as a bridge to other Controllers
             */
            Bridge.label = labelTerm;
            Bridge.schName = labelSchName;
            Bridge.boys = labelBoys;
            Bridge.girls = labelGirl;
            Bridge.students = labelStu;
            Bridge.teachers = labelTeach;
            Bridge.schLogo = imageView;

            //Display Total Student,Boys,Girls
            ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO");
            rs = ps.executeQuery();
            if (rs.next()) {
                labelStu.setText(rs.getString(1));
            }
            ps.close();

            ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO Where Sex='M'");
            rs = ps.executeQuery();
            if (rs.next()) {
                labelBoys.setText(rs.getString(1));
            }
            ps.close();

            ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO Where Sex='F'");
            rs = ps.executeQuery();
            if (rs.next()) {
                labelGirl.setText(rs.getString(1));
            }
            ps.close();

            System.out.println("Background Task Done!!!");
        } catch (Exception e) {
        }

    }

    @FXML
    private void studentAction() {
//        if (stdInfo == null) {
//            stdInfo = new SchoolStudentDetails();
//        }
        bPane.setCenter(rootStdInfo);
    }

    @FXML
    private void scoreSheetAction() {
//        if (scoreSheet == null) {
//            scoreSheet = new SchoolScoreSheet();
//        }
        bPane.setCenter(rootScoreSheet);
    }

    @FXML
    private void reportAction() {
//        if (stdReTab == null) {
//            stdReTab = new SchoolReportTab();
//        }
        bPane.setCenter(rootStdReTab);
    }

    @FXML
    private void mainAction() {
        bPane.setCenter(mainPane);
    }

    @FXML
    private void settingsAction() {
//        if (stdTab == null) {
//            stdTab = new SchoolTab();
//        }
        bPane.setCenter(rootStdTab);

        /*if (tab==null) {
            tab=new Tab();
        }
        bPane.setCenter(tab.getFxml());*/
 /*JFXPopup popup=new JFXPopup();
        
        Label clas=new Label("Class");
        Label subject=new Label("Subject");
        Label term=new Label("Term");
        
        VBox vBox=new VBox(clas,subject,term);
        
        popup.setPopupContent(vBox);
        popup.show(butSettings,JFXPopup.PopupVPosition.BOTTOM,JFXPopup.PopupHPosition.RIGHT);
        
        clas.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            if (stdClass==null) {
                stdClass=new SchoolStudentClass();
            }
            bPane.setCenter(stdClass.getPane());
        });
        
        subject.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            if (stdSub==null) {
                stdSub=new SchoolStudentSubject();
            }
            bPane.setCenter(stdSub.getFxml());
        });
        
        term.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            if (stdterm==null) {
                stdterm=new SchoolTerm();
            }
            bPane.setCenter(stdterm.getFxml());
        });*/
    }
}
