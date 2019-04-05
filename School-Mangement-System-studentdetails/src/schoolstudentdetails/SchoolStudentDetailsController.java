/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentdetails;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolStudentDetailsController implements Initializable {
    
    @FXML private ComboBox className;
    @FXML private Label labelID,labelName,labelSex,labelAge,labelParent,labelAddress,labelNum;
    @FXML private ImageView image;
    @FXML private JFXListView listView;
    @FXML private TableView<AcademicTable> tableView;
    @FXML private TableColumn columnClass,columnTerm,columnAcaYear,columnRemark;
    @FXML private JFXButton editBut,deleteBut;
    @FXML private TextField search;
    private ObservableList obList,tableObList;
    private Stage stage;
    private Parent root;
    private FXMLLoader loader;
    private PreparedStatement ps;
    private ResultSet res;
    private String selectedName;
    private List<String> searchList;
    private Image defaultImage;
    private String classCombo,selectedTerm,selectedAcaYear;
    private File file;
    private SuggestionProvider suggestions;
    private Calendar cal;
    
    //fill search Auto Complete textfield with Student name
    private void searchStudent() {
        try {
            ps=SchoolDataBase.getCon().prepareStatement("select name from STUDENTINFO");
            res=ps.executeQuery();
            
            searchList.clear();
            while(res.next()){
                searchList.add(res.getString("name"));
            }
            ps.close();
            
            suggestions.clearSuggestions();
            suggestions.addPossibleSuggestions(searchList);
        } catch (Exception e) {}
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        obList=FXCollections.observableArrayList();
        listView.setItems(obList);
        
        cal=new GregorianCalendar();
        
        searchList=new ArrayList();
        suggestions=SuggestionProvider.create(searchList);
        
        //pass class combo box to labelbridge combo box
        Bridge.comboStuClass=className;
        
        try {
                    Platform.runLater(() -> {
                        stage=new Stage();
                        stage.setResizable(false);
                    });
                    

                    //fill combo box with classes
                    ps=SchoolDataBase.getCon().prepareStatement("select * from STUDENTCLASS");
                    res=ps.executeQuery();
                    className.getItems().clear();
                    while(res.next()){
                        className.getItems().add(res.getString("class"));
                    }
                    
                    ps.close();

                    //fill search Auto Complete textfield with Student name
                    searchStudent();
                    AutoCompletionTextFieldBinding autoCompletionTextFieldBinding = new AutoCompletionTextFieldBinding<>(search,suggestions);
                    //TextFields.bindAutoCompletion(search, searchList);
                    /*TextFields.bindAutoCompletion(search, t -> {
                    return searchList.stream().filter(elem -> {
                    return elem.startsWith(t.getUserText());
                    }).collect(Collectors.toList());
                    });*/

                    ps.close();
                    res.close();
                } catch (Exception e) {}

                editBut.setDisable(true);
                deleteBut.setDisable(true);

                //get empty image
                defaultImage=image.getImage();

                //create file folder to save student photos
                new File(System.getProperty("user.home")+File.separator+"StudentdbPhotos").mkdirs();

                //Resize column when ever table is resized
                columnClass.prefWidthProperty().bind(tableView.widthProperty().divide(4));
                columnTerm.prefWidthProperty().bind(tableView.widthProperty().divide(4));
                columnAcaYear.prefWidthProperty().bind(tableView.widthProperty().divide(4));
                columnRemark.prefWidthProperty().bind(tableView.widthProperty().divide(4));

                tableObList=FXCollections.observableArrayList();
                tableView.setItems(tableObList);

                columnClass.setCellValueFactory(new PropertyValueFactory("clas"));
                columnTerm.setCellValueFactory(new PropertyValueFactory("term"));
                columnAcaYear.setCellValueFactory(new PropertyValueFactory("acaYear"));
                columnRemark.setCellValueFactory(new PropertyValueFactory("remark"));
        
        try {
            //When Student is Selected thats when the Search Action begins
            search.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                searchAction();
            });
            
            //Output student info when student name is been selected
            listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                if (newValue==null) {

                }
                else{
                    selectedName=newValue.toString();
                    showStuInfo();
                    search.clear();
                }
            });
            
            tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends AcademicTable> observable, AcademicTable oldValue, AcademicTable newValue) -> {
                if (newValue==null) {

                }else{
                    selectedTerm=newValue.getTerm();
                    selectedAcaYear=newValue.getAcaYear();
                }
            });
            
        } catch (Exception e) {}
    } 
    
    //Display student infomation
    private void showStuInfo(){
        try {
            tableObList.clear();
            
            //display student Academic Report
            ps=SchoolDataBase.getCon().prepareStatement("select Distinct Class,Term,Academic_Year,Remark from AcademicReport where Student_Name=? and Class=?");
            ps.setString(1, selectedName);
            ps.setString(2, classCombo);
            res=ps.executeQuery();
            
            while(res.next()){
                tableObList.add(new AcademicTable(res.getString("Class"),res.getString("Term"),res.getString("Academic_Year"),res.getString("Remark")));
            }
            ps.close();
            res.close();
            
            //display Student Info
            ps=SchoolDataBase.getCon().prepareStatement("select * from STUDENTINFO where name=?");
            ps.setString(1, selectedName);
            res=ps.executeQuery();

            while(res.next()){
                labelID.setText(res.getInt("id")+"");
                labelName.setText(res.getString("name"));
                labelSex.setText(res.getString("sex"));

                int ag=cal.get(Calendar.YEAR)-res.getDate("age").toLocalDate().getYear();
                labelAge.setText(ag+" years");

                /*
                get Image path from database
                Use ImageIO from Swing to read the file, assign file the BufferedImage
                Convert image to FXimage
                assign the image to Image object
                set the image to ImageView
                */
                file=new File(res.getString("photo"));
                BufferedImage read=ImageIO.read(file);
                Image photo=SwingFXUtils.toFXImage(read, null);
                image.setImage(photo);
                
                labelParent.setText(res.getString("parent"));
                labelAddress.setText(res.getString("address"));
                labelNum.setText(res.getString("number"));
            }
            ps.close();
            res.close();
            editBut.setDisable(false);
            deleteBut.setDisable(false);
            
            //get avarage total from score sheet of each student
            //the reason for this is to output the actual Remark value from their Grading System
            ps=SchoolDataBase.getCon().prepareStatement("select Avg(Total) from ScoreSheet where STUDENT_NAME=? and Class=?");
            ps.setString(1, selectedName);
            ps.setString(2, classCombo);
            res=ps.executeQuery();
            
            res.next();
            int avg=res.getInt(1);//Holds the value
            ps.close();
            res.close();
            
            List sList,rList;
            sList=new ArrayList();
            rList=new ArrayList();
            
            //Get their Score and Remark form Grading System
            //Test to see if the student average is >= to any of the Score and input Remark to DataBase
            ps=SchoolDataBase.getCon().prepareStatement("Select Score,Remark from GradingSystem");
            res=ps.executeQuery();
            while(res.next()){
                sList.add(res.getString("Score").substring(0, 2));
                rList.add(res.getString("Remark"));
                
                //replace "0-" with "0" because the Grade result set last value will be "0-",  
                if (sList.contains("0-")) {
                    sList.remove("0-");
                    sList.add("0");
                }
            }
            ps.close();
            
            //Insert into Remark with the average student score
            ps=SchoolDataBase.getCon().prepareStatement("UPDATE ACADEMICREPORT set REMARK=? where STUDENT_NAME=? and CLASS=?");
            for (int i = 0; i < sList.size(); i++) {
                if(avg>=Integer.parseInt(sList.get(i).toString())){
                    ps.setString(1, rList.get(i).toString());
                    break;
                }
            }
            if (sList.isEmpty()) {
                ps.setString(1, "");
            }
            ps.setString(2, selectedName);
            ps.setString(3, classCombo);
            ps.executeUpdate();
            ps.close();
            
            //display student Academic Report
            ps=SchoolDataBase.getCon().prepareStatement("select Distinct Class,Term,Academic_Year,Remark from AcademicReport where Student_Name=? and Class=?");
            ps.setString(1, selectedName);
            ps.setString(2, classCombo);
            res=ps.executeQuery();
            tableObList.clear();
            while(res.next()){
                tableObList.add(new AcademicTable(res.getString("Class"),res.getString("Term"),res.getString("Academic_Year"),res.getString("Remark")));
            }
            ps.close();
            res.close();
        } catch (Exception ex) {}
    }
    
    @FXML
    private void searchAction(){
        try {
            //set class combo box value to the class which the Student name is been searched for
            //that will automatically populate the list with student names in that class
            String searchName=search.getText();
            ps=SchoolDataBase.getCon().prepareStatement("select class from STUDENTINFO where name=?");
            ps.setString(1, searchName);
            res=ps.executeQuery();
            
            if(res.next()){
                className.setValue(res.getString("class"));
                listView.getSelectionModel().select(searchName);//Automatically select the actual name that is been searched for and display the info
            }
            ps.close();
            res.close();
        } catch (Exception e) {}
    }
    
    //populate ListView with student name
    private void showList(){
        try {
            classCombo=className.getValue().toString();
            
            ps=SchoolDataBase.getCon().prepareStatement("select name from StudentInfo where Class=?");
            ps.setString(1, classCombo);
            res=ps.executeQuery();

            obList.clear();
            while(res.next()){
                obList.add(res.getString("name"));
                //obList.add(new ListItems(res.getString("name")));
            }
            
            //adds the newly added student name the search list;
            //suggestions.addPossibleSuggestions(obList);
            
            ps.close();
            res.close();
            
            labelName.setText("");
            labelSex.setText("");
            labelAddress.setText("");
            labelAge.setText("");
            labelNum.setText("");
            labelParent.setText("");
            labelID.setText("");
            image.setImage(defaultImage);
            tableObList.clear();
        } catch (Exception e) {}
    }
    
    @FXML
    private void classNameAction(){
        try {
            editBut.setDisable(true);
            deleteBut.setDisable(true);

            showList();
            className.setValue(classCombo);
        } catch (Exception e) {}
    }
    
    //method to get and load fxml
    private void openFXML(String fxml) throws IOException{
        loader=new FXMLLoader(getClass().getResource(fxml));
        root=loader.load();
    }
    
    //method to make the fxml visible
    public void showScene(Parent parent, String title){
        if (stage.getModality().equals(Modality.APPLICATION_MODAL)) {
            stage.setScene(new Scene(parent));
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
        //refreah list when ever Add Stage lose focus
        stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (oldValue) {
                //listView.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                showList();
                showStuInfo();
                searchStudent();
            }
        });
        
        try {
            openFXML("AddStudent.fxml");
            AddStudentController addCon=loader.getController();
            addCon.comboValues(className.getItems());//pass classes to Add Student controller combo box
            showScene(root,"Add Student");
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction(){
        //refreah list when ever Edit Stage lose focus
        stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (oldValue) {
                //listView.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                showList();
                showStuInfo();
                searchStudent();
            }
        });
        
        try {
            openFXML("EditStudent.fxml");
            EditStudentController editCon=loader.getController();
            editCon.comboValues(className.getItems());//pass classes to Edit Student controller combo box
            editCon.selectedName(selectedName);
            showScene(root,"Edit Student");
        } catch (Exception e) {}
    }
    
    @FXML
    private void deleteAction(){
        try {
            SchoolAlertDialog.showDeleteAlert(Alert.AlertType.WARNING, "Delete Student", "Deleting this Student will Delete all infomation "
                    + "related to this Student!!! Are you sure you want to Delete "+selectedName+"?");
            
            ButtonType yes=new ButtonType("Yes");
            ButtonType no=new ButtonType("No");
            
            SchoolAlertDialog.alertObject().getButtonTypes().setAll(yes,no);
            Optional<ButtonType> op=SchoolAlertDialog.showAndwait();
            if (op.get().equals(yes)) {
                ps=SchoolDataBase.getCon().prepareStatement("delete from ScoreSheet where student_name=?");
                ps.setString(1, selectedName);
                ps.executeUpdate();
                ps.close();
                
                ps=SchoolDataBase.getCon().prepareStatement("delete from Report where student_name=?");
                ps.setString(1, selectedName);
                ps.executeUpdate();
                ps.close();
                
                ps=SchoolDataBase.getCon().prepareStatement("delete from AcademicReport where student_name=?");
                ps.setString(1, selectedName);
                ps.executeUpdate();
                ps.close();
                
                ps=SchoolDataBase.getCon().prepareStatement("delete from StudentInfo where name=? and id=? and class=?");
                ps.setString(1, selectedName);
                ps.setString(2, labelID.getText());
                ps.setString(3, classCombo);
                ps.executeUpdate();
                ps.close();
                
                file.delete();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Delete Student", "Student Deleted");
                showList();
                
                editBut.setDisable(true);
                deleteBut.setDisable(true);
                
                ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO");
                res = ps.executeQuery();
                if (res.next()) {
                    bridge.Bridge.students.setText(res.getString(1));
                }
                ps.close();
                    
                ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO Where Sex='M'");
                res = ps.executeQuery();
                if (res.next()) {
                    bridge.Bridge.boys.setText(res.getString(1));
                }
                ps.close();
                    
                ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO Where Sex='F'");
                res = ps.executeQuery();
                if (res.next()) {
                    bridge.Bridge.girls.setText(res.getString(1));
                }
                ps.close();
                searchStudent();
            }  
        } catch (Exception e) {}
    }
    
    @FXML private void tableClicked(){
        try {
            openFXML("StudentAcademicReport.fxml");
            StudentAcademicReportController stu=loader.getController();
            stu.getValues(selectedName, classCombo,selectedTerm,selectedAcaYear);
            showScene(root, "Academic Report");
        } catch (Exception e) {}
    }
}
