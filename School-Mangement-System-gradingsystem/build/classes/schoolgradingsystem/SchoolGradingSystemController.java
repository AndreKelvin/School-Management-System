/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgradingsystem;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolGradingSystemController implements Initializable {
    
    @FXML private TableView<Grade> tableView;
    @FXML private TableColumn columnScore;
    @FXML private TableColumn columnGrade;
    @FXML private TableColumn columnRemark;
    private ObservableList<Grade> obList;
    private PreparedStatement ps;
    private ResultSet res;
    private Stage stage;
    private Parent parent;
    private FXMLLoader loader;
    private Grade selectedScore;
    @FXML private JFXButton butEdit,butDelete;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        butEdit.setDisable(true);
        butDelete.setDisable(true);
        
        columnScore.setCellValueFactory(new PropertyValueFactory("score"));
        columnGrade.setCellValueFactory(new PropertyValueFactory("grade"));
        columnRemark.setCellValueFactory(new PropertyValueFactory("remark"));
        
        //Automatically resize columns when table is resized
        /*columnScore.prefWidthProperty().bind(tableView.widthProperty().divide(3));
        columnGrade.prefWidthProperty().bind(tableView.widthProperty().divide(3));
        columnRemark.prefWidthProperty().bind(tableView.widthProperty().divide(3));*/
        
        //populate Table
        obList=FXCollections.observableArrayList();
        tableView.setItems(obList);
        
        refreshObList();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Grade> observable, Grade oldValue, Grade newValue) -> {
            selectedScore=newValue;
            butEdit.setDisable(false);
            butDelete.setDisable(false);
        });
        
        Platform.runLater(() -> {
            stage=new Stage();
            stage.setResizable(false);
        });
    }   
    
    private void refreshObList(){
        try {
            obList.clear();
            ps=SchoolDataBase.getCon().prepareStatement("Select * from GradingSystem");
            res=ps.executeQuery();
            
            while(res.next()){
               obList.add(new Grade(res.getString("Score"), res.getString("Grade"), res.getString("Remark")));
            }
            ps.close();
        } catch (Exception e) {}
    }
    
    private void loadFxml(String fxml) throws IOException{
        loader=new FXMLLoader(getClass().getResource(fxml));
        parent=loader.load();
    }
    
    private void showFxml(Parent parent, String title){
        if (stage.getModality().equals(Modality.APPLICATION_MODAL)) {
            stage.setScene(new Scene(parent));
            stage.setTitle(title);
            stage.showAndWait();
        }
        else{
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(stage.getOwner());
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(new Scene(parent));
            stage.setTitle(title);
            stage.showAndWait();
        }
    }
    
    @FXML
    private void addAction(){
        try {
            loadFxml("InputGrade.fxml");
            InputGradeController input=loader.getController();
            input.getObList(obList);
            showFxml(parent,"Add Grade");
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction(){
        try {
            stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                butEdit.setDisable(true);
                butDelete.setDisable(true);
            });
            loadFxml("EditGrade.fxml");
            EditGradeController edit=loader.getController();
            edit.getSelectedGrade(selectedScore);
            showFxml(parent,"Edit Grade");
        } catch (Exception e) {}
    }
    
    @FXML
    private void deleteAction(){
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Grading System", "Are you sure you to Delete?");
            
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");
            
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);
            
            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("Delete from GradingSystem Where score=?");
                ps.setString(1, selectedScore.toString());
                ps.executeUpdate();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Grading System", "Grade Deleted");
                refreshObList();
                
                butEdit.setDisable(true);
                butDelete.setDisable(true);
            }
        } catch (Exception e) {}
    }
}
