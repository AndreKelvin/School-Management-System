/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolteacher;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.awt.image.BufferedImage;
import java.io.File;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolTeacherController implements Initializable {
    
    @FXML private JFXListView listView;
    @FXML private JFXButton butEdit,butDelete;
    @FXML private Label labelName,labelAddress,labelEmail,labelNum,labelNat,labelSpName,labelChildren,labelMStatus,labelJoined,labelSalary,labelQual,labelGender,labelBirth;
    @FXML private ImageView imageView;
    @FXML private TableView<Teacher> tableView;
    @FXML private TableColumn columnSubject,columnClass;
    private ObservableList obList;
    private ObservableList<Teacher> tableObList;
    private PreparedStatement ps;
    private ResultSet rs;
    private Statement stm;
    private File file;
    private Stage stage;
    private Parent parent;
    private FXMLLoader loader;
    private String selectedName;
    private Image defaultImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        defaultImage=imageView.getImage();
        
        obList=FXCollections.observableArrayList();
        listView.setItems(obList);
        
        tableObList=FXCollections.observableArrayList();
        tableView.setItems(tableObList);
        
        columnSubject.setCellValueFactory(new PropertyValueFactory("subject"));
        columnClass.setCellValueFactory(new PropertyValueFactory("clas"));
            
        butEdit.setDisable(true);
        butDelete.setDisable(true);
        
        //Create Folder to save Teachers photos
        new File(System.getProperty("user.home")+File.separator+"TeachersdbPhotos").mkdirs();
        
        //populate list view with teachers name
        try {
            showTeachers();
            
            //display Teachers info
            listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    selectedName=newValue.toString();
                    
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct Name,Date_Of_Birth,Gender,Qualification,Salary,Date_Joined,"
                                                                             + "Marital_Status,Children,Spouse,Nationality,Number,Email,Address,Photo "
                                                                             + "From Teacher Where name=?");
                    ps.setString(1, selectedName);
                    rs=ps.executeQuery();

                    while (rs.next()) {
                        labelName.setText(rs.getString("Name"));
                        labelBirth.setText(rs.getDate("Date_Of_Birth").toString());
                        labelGender.setText(rs.getString("Gender"));
                        labelQual.setText(rs.getString("Qualification"));
                        labelSalary.setText(rs.getString("Salary"));
                        labelJoined.setText(rs.getString("Date_Joined"));
                        labelMStatus.setText(rs.getString("Marital_Status"));
                        labelChildren.setText(rs.getByte("Children")+"");
                        labelSpName.setText(rs.getString("Spouse"));
                        labelNat.setText(rs.getString("Nationality"));
                        labelNum.setText(rs.getString("Number")+"");
                        labelEmail.setText(rs.getString("Email"));
                        labelAddress.setText(rs.getString("Address"));

                        /*
                        get Image path from database
                        Use ImageIO from Swing to read the file, assign file the BufferedImage
                        Convert image to FXimage
                        assign the image to Image object
                        set the image to ImageView
                        */
                        file=new File(rs.getString("Photo"));
                        BufferedImage read=ImageIO.read(file);
                        Image image=SwingFXUtils.toFXImage(read, null);
                        imageView.setImage(image);
                    }
                    ps.close();
                    
                    butEdit.setDisable(false);
                    butDelete.setDisable(false);
                    
                    //Populate table view with Class and Subject the teacher is Associated with
                    ps=SchoolDataBase.getCon().prepareStatement("Select Subject,Class From Teacher Where name=?");
                    ps.setString(1, newValue.toString());
                    rs=ps.executeQuery();
                    
                    tableObList.clear();
                    while(rs.next()){
                        tableObList.add(new Teacher(rs.getString("Subject"), rs.getString("Class")));
                    }
                } catch (Exception e) {}
            });
            Platform.runLater(() -> {
                stage=new Stage();
                stage.setResizable(false);
            });
        } catch (Exception e) {}
    }   
    
    private void showTeachers(){
        try {
            obList.clear();
            stm=SchoolDataBase.getCon().createStatement();
            stm.executeQuery("Select distinct name From Teacher");
            rs=stm.getResultSet();
            
            while(rs.next()){
                obList.add(rs.getString("name"));
            }
        } catch (Exception e) {}
    }
    
    private void loadFxml(String fxml){
        try {
            loader=new FXMLLoader(getClass().getResource(fxml));
            parent=loader.load();
        } catch (Exception e) {}
    }
    
    private void showFxml(Parent parent, String title){
        if (stage.getModality().equals(Modality.APPLICATION_MODAL)) {
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        }
        else{
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.initOwner(stage.getOwner());
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        }
    }
    
    @FXML
    private void butAddAction() {
        try {
            //refresh list view when ever Add Teacher stage lose focus
            stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                showTeachers();
            });
            loadFxml("AddTeacher.fxml");
            showFxml(parent, "Add Teacher");
        } catch (Exception e) {}
    }
    
    @FXML
    private void butEditAction() {
        try {
            //refresh list view when ever Edit Teacher stage lose focus
            stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                showTeachers();
            });
            loadFxml("EditTeacher.fxml");
            EditTeacherController edit=loader.getController();
            edit.getSelectedName(selectedName);
            showFxml(parent, "Edit Teacher");
        } catch (Exception e) {}
    }
    
    @FXML
    private void butDeleteAction() {
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Delete Teacher", "Are you sure you to delete "+selectedName+"?");
            
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);
            
            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("Delete From Teacher Where Name=? and Photo=?");
                ps.setString(1, selectedName);
                ps.setString(2, file.getAbsolutePath());
                ps.executeUpdate();
                file.delete();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Delete Teacher", "Teacher Deleted");
                showTeachers();
                
                labelAddress.setText("");
                labelBirth.setText("");
                labelChildren.setText("");
                labelEmail.setText("");
                labelGender.setText("");
                labelJoined.setText("");
                labelMStatus.setText("");
                labelName.setText("");
                labelNat.setText("");
                labelNum.setText("");
                labelQual.setText("");
                labelSalary.setText("");
                labelSpName.setText("");
                tableView.getItems().clear();
                imageView.setImage(defaultImage);
                
                short num=0;
                ps=SchoolDataBase.getCon().prepareStatement("SELECT Distinct Name FROM TEACHER");
                rs=ps.executeQuery();
                while(rs.next()){
                    num++;
                }
                bridge.Bridge.teachers.setText(num+"");
            }
        } catch (Exception e) {}
    }
}
