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
import java.awt.image.BufferedImage;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.controlsfx.control.CheckComboBox;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class EditTeacherController implements Initializable {

    @FXML private JFXTextField textFieldName,textFieldQual,textFieldSalary,textFieldSpName,textFieldNat,textFieldNum,textFieldEmail,textFieldAddress,textFieldChildren;
    @FXML private JFXDatePicker dateOfBirth,dateJoined;
    @FXML private JFXRadioButton radioM,radioF;
    @FXML private ToggleGroup tgStatus;
    @FXML private JFXRadioButton radioDiv,radioSingle,radioRel,radioMarr;
    @FXML private ToggleGroup tg;
    @FXML private ImageView imageView;
    @FXML private JFXComboBox comboClass;
    @FXML private CheckComboBox<Object> checkComboSub;
    private String SelectedName;
    private PreparedStatement ps;
    private ResultSet rs;
    private List list,clas;
    private Image photo;
    private File chooserImage,file;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list=new ArrayList();
        clas=new ArrayList();
        try {
            //pupolate class combo box with classes
            Statement stm=SchoolDataBase.getCon().createStatement();
            rs=stm.executeQuery("Select * From StudentClass");
            
            while(rs.next()){
                comboClass.getItems().add(rs.getString("Class"));
            }
            
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
                } catch (Exception e) {
                }
            });
        } catch (Exception e) {}
    }    
    
    @FXML
    private void chooseImage(){
        FileChooser ch=new FileChooser();
        ch.setTitle("Choose Photo");
        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jepg"));
        
        chooserImage=ch.showOpenDialog(textFieldAddress.getScene().getWindow());
        
        if (chooserImage!=null) {
            try {
                photo=new Image(chooserImage.toURI().toURL().toString());
                imageView.setImage(photo);
            } catch (Exception e) {}
        }
    }
    
    public void getSelectedName(String selectedName){
        this.SelectedName=selectedName;
        try {
            ps=SchoolDataBase.getCon().prepareStatement("Select distinct Name,Date_Of_Birth,Gender,Qualification,Salary,Date_Joined,"
                                                                     + "Marital_Status,Children,Spouse,Nationality,Number,Email,Address,Class,Photo "
                                                                     + "From Teacher Where name=?");
            ps.setString(1, selectedName);
            rs=ps.executeQuery();
            
            while(rs.next()){
                textFieldName.setText(rs.getString("Name")); 
                dateOfBirth.setValue(rs.getDate("Date_Of_Birth").toLocalDate());
                
                if (rs.getString("Gender").contentEquals("M")) {
                    radioM.setSelected(true);
                }else{
                    radioF.setSelected(true);
                }
                
                textFieldQual.setText(rs.getString("Qualification"));
                textFieldSalary.setText(rs.getString("Salary"));
                dateJoined.setValue(LocalDate.parse(rs.getString("Date_Joined")));
                
                switch (rs.getString("Marital_Status")) {
                    case "Single":
                        radioSingle.setSelected(true);
                        break;
                    case "Married":
                        radioMarr.setSelected(true);
                        break;
                    case "Relationship":
                        radioRel.setSelected(true);
                        break;
                    default:
                        radioDiv.setSelected(true);
                        break;
                }
                
                textFieldChildren.setText(rs.getInt("Children")+"");
                textFieldSpName.setText(rs.getString("Spouse"));
                textFieldNat.setText(rs.getString("Nationality"));
                textFieldNum.setText(rs.getString("Number"));
                textFieldEmail.setText(rs.getString("Email"));
                textFieldAddress.setText(rs.getString("Address"));
                clas.add(rs.getString("Class"));
                
                /*
                Get Image path from database
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
            
            /*
            While Class is been selected
            get all subject associated with that class and teacher
            add them to check combo box and check all of them
            get all Subject from Subject Table Where Class = to ComboClass Value
            test to know if a particular Subjects is in the check combo box
            if not add that subject to the check combo box
            */
            comboClass.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                try {
                    checkComboSub.getItems().clear();
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct Subject From Teacher Where Class=? and Name=?");
                    ps.setString(1, newValue.toString());
                    ps.setString(2, SelectedName);
                    rs=ps.executeQuery();
                    
                    while(rs.next()){
                        list.add(checkComboSub.getItems().add(rs.getString("Subject")));
                    }
                    ps.close(); 
                    ps=SchoolDataBase.getCon().prepareStatement("Select distinct Subject From StudentSubject Where Class=?");
                    ps.setString(1, newValue.toString());
                    rs=ps.executeQuery();
                    
                    while(rs.next()){
                        if (!checkComboSub.getItems().contains(rs.getString("Subject"))) {
                            checkComboSub.getItems().add(rs.getString("Subject"));
                            checkComboSub.checkModelProperty().getValue().clearChecks();
                        }
                    }
                    
                    for (int i = 0; i < list.size(); i++) {
                        checkComboSub.checkModelProperty().getValue().check(i);
                    }
                    list.clear();
                } catch (Exception e) {}
            });
            comboClass.setValue(clas.get(0));// for the change listner to see change in the combo box once the stage comes up
        } catch (Exception e) {}
    }
    
    @FXML
    private void editAction(){
        try {
            String name=textFieldName.getText();
            String qual=textFieldQual.getText();
            String salary=textFieldSalary.getText();
            String nat=textFieldNat.getText();
            String children=textFieldChildren.getText();
            String num=textFieldNum.getText();
            String address=textFieldAddress.getText();
            if (name.isEmpty()||dateOfBirth.getValue()==null||dateJoined.getValue()==null||tg.getSelectedToggle()==null||qual.isEmpty()||salary.isEmpty()||tgStatus.getSelectedToggle()==null||nat.isEmpty()||children.isEmpty()||num.isEmpty()||address.isEmpty()||comboClass.getValue()==null||checkComboSub.checkModelProperty().getValue().getCheckedItems().isEmpty()) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Teacher", "Fill all required input");
            }
            else{
                int len=checkComboSub.checkModelProperty().getValue().getCheckedItems().size();
                for (int i = 0; i < len; i++) {
                    ps=SchoolDataBase.getCon().prepareStatement("Update Teacher Set Name=?,Date_Of_Birth=?,Gender=?,Qualification=?,Salary=?,"
                                                                             + "Date_Joined=?, Marital_Status=?,Children=?,Spouse=?,Nationality=?,Number=?,Email=?,Address=?,"
                                                                             + "Class=?,Subject=?,Photo=? Where Name=?");
                    ps.setString(1, name);
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
                    
                    //save File Chooser Image to file
                    //than save file Path to Database
                    if(chooserImage!=null){//if a image is choosen

                        //check if the recent image exist and delete it.
                        //this will avoid deleting the image everytime an image is Choosen
                        if (file.exists()) {
                            file.delete();//Delete the Exiting Student Photo
                        }

                        InputStream in=new FileInputStream(chooserImage);
                        OutputStream out=new FileOutputStream(System.getProperty("user.home")+File.separator+"TeachersdbPhotos"+File.separator+chooserImage.getName());
                        byte[] data=new byte[1024];
                        int size;
                        while((size=in.read(data))!=-1){//-1 means if the end of the stream is reached
                            out.write(data, 0, size);
                        }
                        out.close();

                        ps.setString(16, System.getProperty("user.home")+File.separator+"TeachersdbPhotos"+File.separator+chooserImage.getName());
                    }else{//if image is not choosen 
                        //save the same the Avilable Image path to DataBase
                        //this will avoid an sql error (Photo=?)
                        ps.setString(16, System.getProperty("user.home")+File.separator+"TeachersdbPhotos"+File.separator+file.getName());
                    }
                    ps.setString(17, SelectedName);
                    ps.executeUpdate();
                }
                    SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Edit Teacher", "Change Succesful");
                    textFieldAddress.getScene().getWindow().hide();
            }
        } catch (Exception e) {}
    }
}
