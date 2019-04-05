/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentsubject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolStudentSubjectController implements Initializable {
    
    private PreparedStatement ps;
    private ResultSet res;
    @FXML private JFXComboBox comboClass;
    @FXML private JFXListView listViewSub;
    @FXML private JFXButton butEdit,butDelete;;
    private String selectedSub;
    private FXMLLoader loader;
    private Parent parent;
    private Stage stage;
    private ObservableList obList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        butEdit.setDisable(true);
        butDelete.setDisable(true);
        
        try {
            //Populate Combo boxes with Class
            ps=SchoolDataBase.getCon().prepareStatement("select * from STUDENTCLASS");
            res=ps.executeQuery();
            
            while(res.next()){
                comboClass.getItems().add(res.getString("Class"));
            }
            ps.close();
            
            comboClass.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                butEdit.setDisable(true);
                butDelete.setDisable(true);
            });
            
            listViewSub.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                if (newValue==null) {
                    
                }
                else{
                    selectedSub=newValue.toString();
                    butEdit.setDisable(false);
                    butDelete.setDisable(false);
                }
            });
            
            //Pass the combo box
            Bridge.comboSubjectClass=comboClass;
            
            Platform.runLater(() -> {
                stage=new Stage();
                stage.setResizable(false);
            });
        } catch (Exception e) {}
    }    
    
    private void refreshList() throws SQLException{
        //Populate List View with Subject
        obList=FXCollections.observableArrayList();
        obList.clear();
        
        ps=SchoolDataBase.getCon().prepareStatement("select subject from STUDENTSUBJECT where class=?");
        ps.setString(1,comboClass.getValue().toString());
        res=ps.executeQuery();
            
        while(res.next()){
            obList.add(res.getString("subject"));
        }
        listViewSub.setItems(obList);
    }
    
    @FXML
    private void comboAction(ActionEvent event) {
        try {
            refreshList();
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
    private void addAction() {
        //refreah list when ever Add Stage lose focus
        comboClass.getScene().getWindow().focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                refreshList();
            } catch (Exception e) {}
        });
        try {
            loadFxml("AddSubject.fxml");
            AddSubjectController add=loader.getController();
            add.getList(comboClass.getItems(),obList);
            showFxml(parent, "Add Subject");
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction() {
        //refreah list when ever Edit Stage lose focus
        comboClass.getScene().getWindow().focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                refreshList();
            } catch (Exception e) {}
        });
        
        try {
            loadFxml("EditSubject.fxml");
            EditSubjectController edit=loader.getController();
            edit.getListandSelectedSub(comboClass.getItems(), selectedSub, comboClass.getValue());
            showFxml(parent, "Edit Subject");
        } catch (Exception e) {}
    }
    
    @FXML
    private void deleteAction() {
        try {
            //Delete Subject
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Subject", "Are you sure you want to Delete this Subject");
        
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");

           SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);

            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("SELECT id FROM StudentSubject where subject=? and class=?");
                ps.setString(1, selectedSub);
                ps.setString(2, comboClass.getValue().toString());
                res=ps.executeQuery();
                
                if(res.next()){
                    ps=SchoolDataBase.getCon().prepareStatement("Delete from StudentSubject where Subject=? and class=? and id=?");
                    ps.setString(1, selectedSub);
                    ps.setString(2, comboClass.getValue().toString());
                    ps.setInt(3, res.getInt("id"));
                    ps.executeUpdate();
                }
                SchoolAlertDialog.showDeleteAlert(Alert.AlertType.CONFIRMATION, "Subject", "Subject Deleted");
                
                refreshList();
                
                butEdit.setDisable(true);
                butDelete.setDisable(true);
            }
        } catch (Exception e) {}
    } 
}
