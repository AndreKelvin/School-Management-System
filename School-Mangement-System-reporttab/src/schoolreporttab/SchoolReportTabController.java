/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolreporttab;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolReportTabController implements Initializable {
    
    @FXML private BorderPane bPaneTable,bPaneGraphy;
    private Parent rootReport,rootChart;
    
    public void getRoots(Parent root1,Parent root2){
        rootReport=root1;
        rootChart=root2;
    }
    
    @FXML
    public void tableAction() {
//        if (report==null) {
//            report=new SchoolReport();
//        }
        bPaneTable.setCenter(rootReport);
    }
    
    @FXML
    private void graphyAction() {
//        if (barChart==null) {
//            barChart=new SchoolChart();
//        }
        bPaneGraphy.setCenter(rootChart);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
