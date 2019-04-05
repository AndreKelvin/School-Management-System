/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentclass;

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
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;


/**
 *
 * @author Andre Kelvin
 */
public class SchoolStudentClassController implements Initializable {
    
    @FXML private TableView<ClassTable> tableClass;
    @FXML private TableColumn columnClass;
    @FXML private JFXButton buttonEdit,buttonDelete;
    private ObservableList obList;
    private PreparedStatement ps;
    private ResultSet res;
    private ClassTable selectedValue;
    private FXMLLoader loader;
    private Parent parent;
    private Stage stage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        obList=FXCollections.observableArrayList();
        Bridge.comboGraphyClass.setItems(obList);
        Bridge.comboReportClass.setItems(obList);
        Bridge.comboScoreSheetClass.setItems(obList);
        Bridge.comboStuClass.setItems(obList);
        Bridge.comboSubjectClass.setItems(obList);
        Bridge.comboStudentFeeClass.setItems(obList);
        tableClass.setItems(obList);
        
        buttonEdit.setDisable(true);
        buttonDelete.setDisable(true);
        
        columnClass.setCellValueFactory(new PropertyValueFactory("className"));
        
        showClasses();
        
        tableClass.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ClassTable> observable, ClassTable oldValue, ClassTable newValue) -> {
            buttonEdit.setDisable(false);
            buttonDelete.setDisable(false);
            if (newValue==null) {
                
            }else{
                selectedValue=newValue;
            }
        });
        
        Platform.runLater(() -> {
            stage=new Stage();
            stage.setResizable(false);
        });
        
        //Automatically resize columns when table is resized
        //columnClass.prefWidthProperty().bind(tableClass.widthProperty());
    }  
    
    private void showClasses(){
        try {
            obList.clear();
            ps=SchoolDataBase.getCon().prepareStatement("select * from STUDENTCLASS");
            res=ps.executeQuery();
            while(res.next()){
                obList.add(new ClassTable(res.getString("class")));
            }
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
        }else{
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
        try {
            loadFxml("AddClass.fxml");
            AddClassController add=loader.getController();
            add.getObList(obList);
            showFxml(parent,"Add Class");
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction() {
        try {
            stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (oldValue) {
                    showClasses();
                }
            });
            
            loadFxml("EditClass.fxml");
            EditClassController edit=loader.getController();
            edit.getSelectedClass(selectedValue);
            showFxml(parent,"Edit Class");
        } catch (Exception e) {}
    }
    
    @FXML
    private void deleteAction(){
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Class", "Are you sure you want to delete");
        
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);

            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("delete from STUDENTCLASS where class=?");
                ps.setString(1, selectedValue.getClassName());
                ps.executeUpdate();

                showClasses();
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Class", "Class Deleted");
                
                buttonEdit.setDisable(true);
                buttonDelete.setDisable(true);
            }
        } catch (Exception e) {}
    }
}
