/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolreport;

import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolReportController implements Initializable {

    @FXML private TableView<Report> tableView;
    @FXML private JFXComboBox comboAcaYear, comboClass, comboTerm;
    @FXML private TableColumn columnName, columnSex, columnTotal, columnAvg, columnPos, columnFailPass, columnRemark;
    @FXML private Label labelTotalStu, labelPercentFail, labelPercentPass, labelNumFail, labelNumPass, labelTotalBoys, labelPercentBoysFail, labelPercentBoysPass;
    @FXML private Label labelBoysFail, labelBoysPass, labelTotalGirls, labelPercentGirlsFail, labelPercentGirlsPass, labelGirlsFail, labelGirlsPass;
    private PreparedStatement ps, ps2;
    private ResultSet rs;
    private ObservableList<Report> obList;
    @FXML private StackPane stackPane;
    @FXML private BorderPane bPane;
    @FXML private ProgressIndicator progress;
    @FXML private Label labelSchName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //Populate Class,Term,AcademYear combo box with their values
            Statement stm = SchoolDataBase.getCon().createStatement();
            stm.executeQuery("Select * From StudentClass");
            rs = stm.getResultSet();
            
            while (rs.next()) {
                comboClass.getItems().add(rs.getString("Class"));
            }
            rs.close();

            stm.executeQuery("Select distinct Academic_Year From StudentTerm");
            rs = stm.getResultSet();

            while (rs.next()) {
                comboAcaYear.getItems().add(rs.getString("Academic_Year"));
            }
            rs.close();
            
            //populate Term combo box when Academic Year is selected
            comboAcaYear.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    comboTerm.getItems().clear();
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct Name From StudentTerm Where Academic_Year=?");
                    ps.setString(1, newValue.toString());
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        comboTerm.getItems().add(rs.getString("Name"));
                    }
                    ps.close();
                } catch (Exception e) {}
            });

            obList = FXCollections.observableArrayList();
            tableView.setItems(obList);

            columnAvg.setCellValueFactory(new PropertyValueFactory("avg"));
            columnFailPass.setCellValueFactory(new PropertyValueFactory("passFail"));
            columnName.setCellValueFactory(new PropertyValueFactory("name"));
            columnPos.setCellValueFactory(new PropertyValueFactory("pos"));
            columnRemark.setCellValueFactory(new PropertyValueFactory("remark"));
            columnSex.setCellValueFactory(new PropertyValueFactory("sex"));
            columnTotal.setCellValueFactory(new PropertyValueFactory("tScore"));
            
            //pass the combo box and school name label
            Bridge.comboReportClass=comboClass;
            Bridge.comboReportAcaYear=comboAcaYear;
            Bridge.schNameReport=labelSchName;
            
            comboAcaYear.setOnShowing((Event event) -> {
                comboAcaYear.getItems().clear();
                try {
                    stm.executeQuery("Select distinct Academic_Year From StudentTerm");
                    rs = stm.getResultSet();

                    while (rs.next()) {
                        comboAcaYear.getItems().add(rs.getString("Academic_Year"));
                    }
                    rs.close();
                } catch (Exception e) {}
            });
            
            //Display School Name
            ps = SchoolDataBase.getCon().prepareStatement("Select * from School_Details");
            rs = ps.executeQuery();
            if (rs.next()) {
                labelSchName.setText(rs.getString("Name").toUpperCase());
            }
            ps.close();
        } catch (Exception e) {}
    }

    //To input st,nd,rd and th to the Position numbers
    private String ordinal(int i) {
        int mod100 = i % 100;
        int mod10 = i % 10;
        if (mod10 == 1 && mod100 != 11) {
            return i + "st";
        } else if (mod10 == 2 && mod100 != 12) {
            return i + "nd";
        } else if (mod10 == 3 && mod100 != 13) {
            return i + "rd";
        } else {
            return i + "th";
        }
    }

    private class Generate extends Task<Object> {

        @Override
        protected Object call() throws Exception {

            Platform.runLater(() -> {
                try {
                    if (comboClass.getValue() == null || comboTerm.getValue() == null || comboAcaYear.getValue() == null) {
                        SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Report", "Invalid input");
                    } else {
                        String classCombo = comboClass.getValue().toString();
                        String termCombo = comboTerm.getValue().toString();
                        String yearCombo = comboAcaYear.getValue().toString();

                        //Insert Position in Report
                        int pos = 1;
                        double val = 0;
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT Average FROM REPORT Where Class=? and Term=? and Academic_Year=?"
                                + " order by Average desc");
                        ps.setString(1, classCombo);
                        ps.setString(2, termCombo);
                        ps.setString(3, yearCombo);
                        rs = ps.executeQuery();

                        while (rs.next()) {
                            //this test takes care of duplicate Average an Inputs the same Position
                            if (val == rs.getDouble("Average")) {
                                pos--;
                            }
                            ps2 = SchoolDataBase.getCon().prepareStatement("update Report set Position=? Where Average=? and Class=? and Term=?"
                                    + " and Academic_Year=?");
                            ps2.setString(1, ordinal(pos));
                            ps2.setDouble(2, rs.getDouble("Average"));
                            ps2.setString(3, classCombo);
                            ps2.setString(4, termCombo);
                            ps2.setString(5, yearCombo);
                            val = rs.getDouble("Average");
                            ps2.executeUpdate();
                            pos++;
                        }
                        ps.close();

                        ps = SchoolDataBase.getCon().prepareStatement("SELECT STUDENT_NAME,SEX,TOTAL_SCORE,AVERAGE,Position,FAIL_PASS,REMARK FROM REPORT"
                                + " WHERE Class=? and TERM=? and ACADEMIC_YEAR=? Order By STUDENT_NAME Asc");
                        ps.setString(1, classCombo);
                        ps.setString(2, termCombo);
                        ps.setString(3, yearCombo);
                        rs = ps.executeQuery();
                        obList.clear();
                        while (rs.next()) {
                            obList.add(new Report(rs.getString("STUDENT_NAME"), rs.getString("Sex"), (short) (rs.getInt("TOTAL_SCORE")), (float) (rs.getDouble("AVERAGE")),
                                    rs.getString("Position"), rs.getString("FAIL_PASS"), rs.getString("Remark")));
                        }
                        ps.close();

                        //Summary Total
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalStu FROM report Where Class=? and Term=? and Academic_Year=?");
                        ps.setString(1, classCombo);
                        ps.setString(2, termCombo);
                        ps.setString(3, yearCombo);
                        rs = ps.executeQuery();
                        rs.next();
                        labelTotalStu.setText(rs.getString("TotalStu"));
                        ps.close();

                        //Total Number Of Student Pass
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalP FROM report Where FAIL_PASS=? and Class=? and Term=?"
                                + " and Academic_Year=?");
                        ps.setString(1, "P");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        rs = ps.executeQuery();
                        rs.next();
                        labelNumPass.setText(rs.getString("TotalP"));
                        ps.close();

                        //Total Number Of Student Fail
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalF FROM report Where FAIL_PASS=? and Class=? and Term=?"
                                + " and Academic_Year=?");
                        ps.setString(1, "F");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        rs = ps.executeQuery();
                        rs.next();
                        labelNumFail.setText(rs.getString("TotalF"));
                        ps.close();

                        //Percent of both Pass and Fail
                        short totalStu = Short.parseShort(labelTotalStu.getText());
                        short perPass = Short.parseShort(labelNumPass.getText());
                        short perFail = Short.parseShort(labelNumFail.getText());
                        labelPercentPass.setText(Math.round((perPass / totalStu) * 100) + "%");
                        labelPercentFail.setText(Math.round((perFail / totalStu) * 100) + "%");

                        //Summary Total of Boys
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalBoys FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=?");
                        ps.setString(1, "M");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        rs = ps.executeQuery();
                        rs.next();
                        labelTotalBoys.setText(rs.getString("TotalBoys"));
                        ps.close();

                        //Total Number Of Boys Pass
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalBoysP FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=? and Fail_Pass=?");
                        ps.setString(1, "M");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        ps.setString(5, "P");
                        rs = ps.executeQuery();
                        rs.next();
                        labelBoysPass.setText(rs.getString("TotalBoysP"));
                        ps.close();

                        //Total Number Of Boys Fail
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalBoysF FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=? and Fail_Pass=?");
                        ps.setString(1, "M");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        ps.setString(5, "F");
                        rs = ps.executeQuery();
                        rs.next();
                        labelBoysFail.setText(rs.getString("TotalBoysF"));
                        ps.close();

                        try {
                            //Percent of both Boys Pass and Boys Fail
                            short totalBoys = Short.parseShort(labelTotalBoys.getText());
                            short perBoysPass = Short.parseShort(labelBoysPass.getText());
                            short perBoysFail = Short.parseShort(labelBoysFail.getText());
                            labelPercentBoysPass.setText(Math.round((perBoysPass / totalBoys) * 100) + "%");
                            labelPercentBoysFail.setText(Math.round((perBoysFail / totalBoys) * 100) + "%");
                        } catch (Exception e) {
                            /*
                            Incease there is no Boys in that class
                            it will throw Arithmetic Exception
                            because perBoysFail or perBoysPass/totalBoys (0/0) = throws error
                             */
                            labelPercentBoysPass.setText("0%");
                            labelPercentBoysFail.setText("0%");
                        }

                        //Summary Total of Girls
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalGirls FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=?");
                        ps.setString(1, "F");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        rs = ps.executeQuery();
                        rs.next();
                        labelTotalGirls.setText(rs.getString("TotalGirls"));
                        ps.close();

                        //Total Number Of Girls Pass
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalGirlsP FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=? and Fail_Pass=?");
                        ps.setString(1, "F");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        ps.setString(5, "P");
                        rs = ps.executeQuery();
                        rs.next();
                        labelGirlsPass.setText(rs.getString("TotalGirlsP"));
                        ps.close();

                        //Total Number Of Girls Fail
                        ps = SchoolDataBase.getCon().prepareStatement("SELECT count(STUDENT_NAME) as TotalGirlsF FROM report Where Sex=? and Class=? and Term=?"
                                + " and Academic_Year=? and Fail_Pass=?");
                        ps.setString(1, "F");
                        ps.setString(2, classCombo);
                        ps.setString(3, termCombo);
                        ps.setString(4, yearCombo);
                        ps.setString(5, "F");
                        rs = ps.executeQuery();
                        rs.next();
                        labelGirlsFail.setText(rs.getString("TotalGirlsF"));
                        ps.close();

                        try {
                            //Percent of both Girls Pass and Girls Fail
                            short totalGirls = Short.parseShort(labelTotalGirls.getText());
                            short perGirlsPass = Short.parseShort(labelGirlsPass.getText());
                            short perGirlsFail = Short.parseShort(labelGirlsFail.getText());
                            labelPercentGirlsPass.setText(Math.round((perGirlsPass / totalGirls) * 100) + "%");
                            labelPercentGirlsFail.setText(Math.round((perGirlsFail / totalGirls) * 100) + "%");
                        } catch (Exception e) {
                            /*
                            Incease there is no Girls in that class
                            it will throw Arithmetic Exception
                            because perGirlsFail or perGirlsPass/totalGirls (0/0) = throws error
                             */
                            labelPercentGirlsPass.setText("0%");
                            labelPercentGirlsFail.setText("0%");
                        }

                        ps = SchoolDataBase.getCon().prepareStatement("Delete from Summary Where Class=? and Term=? and Academic_Year=?");
                        ps.setString(1, classCombo);
                        ps.setString(2, termCombo);
                        ps.setString(3, yearCombo);
                        ps.executeUpdate();
                        ps.close();

                        ps = SchoolDataBase.getCon().prepareStatement("Insert into Summary Values(?,?,?,?,?,?,?,?,?,?,?,?)");
                        ps.setInt(1, Integer.parseInt(labelTotalStu.getText()));
                        ps.setInt(2, Integer.parseInt(labelNumPass.getText()));
                        ps.setInt(3, Integer.parseInt(labelNumFail.getText()));
                        ps.setInt(4, Integer.parseInt(labelTotalBoys.getText()));
                        ps.setInt(5, Integer.parseInt(labelBoysPass.getText()));
                        ps.setInt(6, Integer.parseInt(labelBoysFail.getText()));
                        ps.setInt(7, Integer.parseInt(labelTotalGirls.getText()));
                        ps.setInt(8, Integer.parseInt(labelGirlsPass.getText()));
                        ps.setInt(9, Integer.parseInt(labelGirlsFail.getText()));
                        ps.setString(10, classCombo);
                        ps.setString(11, termCombo);
                        ps.setString(12, yearCombo);
                        ps.executeUpdate();
                    }
                } catch (Exception e) {}
            });

            return null;
        }

    }

    @FXML
    private void genAction() {
        bPane.setDisable(true);
        progress.setVisible(true);

        Generate task = new Generate();
        task.setOnSucceeded((event) -> {
            bPane.setDisable(false);
            progress.setVisible(false);
        });
        new Thread(task).start();
    }

    @FXML
    private void printAction() {
        PrinterJob print = PrinterJob.createPrinterJob();
        if (print != null && print.showPrintDialog(comboAcaYear.getScene().getWindow())) {
            //print.showPageSetupDialog(bPane.getScene().getWindow());
            //print.showPrintDialog(bPane.getScene().getWindow());
            if (print.printPage(stackPane)) {
                //print.printPage(bPane);
                print.endJob();
            }
        }
    }
}
