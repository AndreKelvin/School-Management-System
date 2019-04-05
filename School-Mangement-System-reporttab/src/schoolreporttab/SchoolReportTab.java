/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolreporttab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schoolchart.SchoolChart;
import schoolreport.SchoolReport;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolReportTab extends Application {
    
    private Parent root;
    private SchoolReport report;
    private SchoolChart barChart;
    private FXMLLoader loader;
    
    @Override
    public void start(Stage stage) throws Exception {
        //root = FXMLLoader.load(getClass().getResource("SchoolReportTab.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    public SchoolReportTab() {
        try {
            report=new SchoolReport();
            barChart=new SchoolChart();
            loader =new FXMLLoader(getClass().getResource("SchoolReportTab.fxml"));
            root=loader.load();
            SchoolReportTabController tab=loader.getController();
            tab.getRoots(report.getFxml(), barChart.getFxml());
            tab.tableAction();
        } catch (Exception e) {}
    }
    
    public Parent getFxml(){
        return root;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
