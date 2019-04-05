/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolteacher;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class AddTeacherController implements Initializable {

    @FXML private JFXTextField textFieldName,textFieldQual,textFieldSalary,textFieldSpName,textFieldNat,textFieldNum,textFieldEmail,textFieldAddress,textFieldChildren;
    @FXML private JFXDatePicker dateOfBirth,dateJoined;
    @FXML private JFXRadioButton radioM,radioF,radioDiv,radioMarr,radioSingle,radioRel;
    @FXML private ToggleGroup tg,tgStatus;
    @FXML private ImageView imageView;
    @FXML private JFXComboBox comboClass;
    @FXML private CheckComboBox<Object> checkComboSub;
    private ObservableList classList;
    private ResultSet rs;
    private PreparedStatement ps;
    private File file;
    private Image photo,defaultImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        classList=FXCollections.observableArrayList();
        try {
            //populate class combo box with with classes
            Statement stm=SchoolDataBase.getCon().createStatement();
            rs=stm.executeQuery("Select * From StudentClass");
            
            while(rs.next()){
                classList.add(rs.getString("Class"));
            }
            comboClass.setItems(classList);
            
            //while class is been selected Subjects related to the class will be in check combo box
            comboClass.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    checkComboSub.checkModelProperty().getValue().clearChecks();
                    checkComboSub.getItems().clear();
                    ps=SchoolDataBase.getCon().prepareStatement("Select Subject From StudentSubject Where Class=?");
                    ps.setString(1, newValue.toString());
                    rs=ps.executeQuery();
                    
                    while(rs.next()){
                        checkComboSub.getItems().add(rs.getString("Subject"));
                    }
                } catch (Exception e) {}
            });
            
            //set system date to text field
//            SimpleDateFormat date=new SimpleDateFormat("d/MM/Y");
//            textFieldJoined.setText(date.format(new Date()));
            
            //Force the text field to numeric only
            textFieldNum.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if(!newValue.matches("\\d*")){
                    textFieldNum.setText(newValue.replaceAll("[^\\d]", ""));
                    Toolkit.getDefaultToolkit().beep();
                }
            });
            
            textFieldChildren.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if(!newValue.matches("\\d*")){
                    textFieldChildren.setText(newValue.replaceAll("[^\\d]", ""));
                    Toolkit.getDefaultToolkit().beep();
                }
            });
            
            //input comma sign(if needed)
            textFieldSalary.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                try {
                    if (oldValue) {//when text lose focus
                    String comma=new DecimalFormat(",000").format(Integer.parseInt(textFieldSalary.getText()));
                    textFieldSalary.setText(comma);
                }
                    if (newValue) {//when text gain focus
                        if (textFieldSalary.getText().contains(",")) {
                            textFieldSalary.setText(textFieldSalary.getText().replaceAll(",", ""));
                        }
                    }
                } catch (Exception e) {}
            });
            
            //get the default image from Image View
            //to be displayed when user has succesfully added a teacher
            defaultImage=imageView.getImage();
        } catch (Exception e) {}
    }    

    @FXML
    private void chooseImage(){
        FileChooser ch=new FileChooser();
        ch.setTitle("Choose Photo");
        
        //Specifi the extention type
        ch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jepg"));
        
        //open the chooser Dialog
        file=ch.showOpenDialog(textFieldAddress.getScene().getWindow());
        
        if (file!=null) {
            try {
                photo=new Image(file.toURI().toURL().toString());
                imageView.setImage(photo);
            } catch (Exception e) {}
        }
    }
    
    @FXML
    private void addAction(){
        try {
            String name=textFieldName.getText();
            String qual=textFieldQual.getText();
            String salary=textFieldSalary.getText();
            String nat=textFieldNat.getText();
            String children=textFieldChildren.getText();
            String num=textFieldNum.getText();
            String address=textFieldAddress.getText();
            if (name.isEmpty()||dateOfBirth.getValue()==null||dateJoined.getValue()==null||tg.getSelectedToggle()==null||qual.isEmpty()||salary.isEmpty()||tgStatus.getSelectedToggle()==null||nat.isEmpty()||children.isEmpty()||num.isEmpty()||address.isEmpty()||comboClass.getValue()==null||checkComboSub.checkModelProperty().getValue().getCheckedItems().isEmpty()||imageView.getImage()==defaultImage) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Teacher", "Fill all required input");
            }
            else{
                int len=checkComboSub.checkModelProperty().getValue().getCheckedItems().size();
                for (int i = 0; i < len; i++) {
                    ps=SchoolDataBase.getCon().prepareStatement("Insert Into Teacher Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, textFieldName.getText());
                    ps.setDate(2, java.sql.Date.valueOf(dateOfBirth.getValue()));

                    if (radioM.isSelected()) {
                        ps.setString(3, radioM.getText());
                    }else{
                        ps.setString(3, radioF.getText());
                    }

                    ps.setString(4, qual);
                    ps.setString(5, salary);
                    ps.setString(6, dateJoined.getValue().toString());

                    if (radioSingle.isSelected()) {
                        ps.setString(7, radioSingle.getText());
                    }else if(radioMarr.isSelected()){
                        ps.setString(7, radioMarr.getText());
                    }else if(radioRel.isSelected()){
                        ps.setString(7, radioRel.getText());
                    }else{
                        ps.setString(7, radioDiv.getText());
                    }

                    ps.setInt(8, Integer.parseInt(children));
                    ps.setString(9, textFieldSpName.getText());
                    ps.setString(10, nat);
                    ps.setString(11, num);
                    ps.setString(12, textFieldEmail.getText());
                    ps.setString(13, address);
                    ps.setString(14, comboClass.getValue().toString());
                    ps.setString(15, checkComboSub.checkModelProperty().getValue().getCheckedItems().get(i).toString());

                    /*
                    get image path, Save the path to DataBase
                    Save the actual Image to TeacherdbPhotos file
                    */
                    ps.setString(16, System.getProperty("user.home")+File.separator+"TeachersdbPhotos"+File.separator+file.getName());
                    ps.executeUpdate();
                }
                ps.close();
                
                //now save the actual image to file
                InputStream in=new FileInputStream(file);
                OutputStream out=new FileOutputStream(System.getProperty("user.home")+File.separator+"TeachersdbPhotos"+File.separator+file.getName());
                byte[] data=new byte[1024];
                int size;
                while((size=in.read(data))!=-1){//-1 means if the end of the stream is reached
                    out.write(data, 0, size);
                }
                out.close();
                
                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Add Teacher", "Add Succesful");
                imageView.setImage(defaultImage);
                textFieldAddress.clear();
                textFieldChildren.clear();
                textFieldEmail.clear();
                textFieldName.clear();
                textFieldNat.clear();
                textFieldNum.clear();
                textFieldQual.clear();
                textFieldSalary.clear();
                textFieldSpName.clear();
                tg.getToggles().clear();
                dateOfBirth.setValue(null);
                dateJoined.setValue(null);
                tgStatus.getToggles().clear();
                comboClass.setValue(null);
                checkComboSub.getCheckModel().clearChecks();
                
                short teach=0;
                ps=SchoolDataBase.getCon().prepareStatement("SELECT Distinct Name FROM TEACHER");
                rs=ps.executeQuery();
                while(rs.next()){
                    teach++;
                }
                bridge.Bridge.teachers.setText(teach+"");
            }
        } catch (Exception e) {}
    }
}
