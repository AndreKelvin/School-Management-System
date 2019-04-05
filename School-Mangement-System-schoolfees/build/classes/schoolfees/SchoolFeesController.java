/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolfees;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class SchoolFeesController implements Initializable {
    
    @FXML private JFXButton butEdit,butDelete;
    @FXML private TableView<Fee> tableView;
    @FXML private TableColumn columnClass,columnFee;
    private PreparedStatement ps;
    private ResultSet rs;
    private ObservableList<Fee> obList;
    private Stage stage;
    private Parent parent;
    private FXMLLoader loader;
    private Fee selectedValue;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Platform.runLater(() -> {
                stage=new Stage();
                stage.setResizable(false);
            });
            
            butEdit.setDisable(true);
            butDelete.setDisable(true);
            
            obList=FXCollections.observableArrayList();
            tableView.setItems(obList);
            
            columnClass.setCellValueFactory(new PropertyValueFactory("clas"));
            columnFee.setCellValueFactory(new PropertyValueFactory("fee"));
            
            showFees();
            
            tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Fee> observable, Fee oldValue, Fee newValue) -> {
                selectedValue=newValue;
                butEdit.setDisable(false);
                butDelete.setDisable(false);
            });
        } catch (Exception e) {}
    }  
    
    private void showFees(){
        try {
            obList.clear();
            Statement stm=SchoolDataBase.getCon().createStatement();
            rs=stm.executeQuery("Select * From SchoolFee");
            
            while(rs.next()){
                obList.add(new Fee(rs.getString("Class"), rs.getString("Fee")));
            }
        } catch (Exception e) {}
    }
    
    private void loadFxml(String fxml) throws IOException{
        loader=new FXMLLoader(getClass().getResource(fxml));
        parent=loader.load();
    }
        
    private void showFxml(Parent parent,String title){
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
    private void addAction(ActionEvent event) {
        try {
            loadFxml("AddFee.fxml");
            AddFeeController add=loader.getController();
            add.getObList(obList);
            showFxml(parent, "Add Fee");
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction(ActionEvent event) {
        try {
            loadFxml("EditFee.fxml");
            EditFeeController edit=loader.getController();
            edit.getSelectedValue(selectedValue);
            showFxml(parent, "Edit Fee");
        } catch (Exception e) {}
    }
    
    @FXML
    private void deleteAction(ActionEvent event) {
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "School Fees", "Are you sure you want to Delete?");
            
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);
            
            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("Delete From SchoolFee Where Class=? and Fee=?");
                ps.setString(1, selectedValue.getClas());
                ps.setString(2, selectedValue.getFee());
                ps.executeUpdate();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "School Fees", "Deleted");
                showFees();
            }
        } catch (Exception e) {}
    }  
}
