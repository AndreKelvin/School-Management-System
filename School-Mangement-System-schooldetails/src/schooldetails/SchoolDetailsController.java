/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldetails;

import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import bridge.Bridge;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolDetailsController implements Initializable {
    
    @FXML private ImageView imageView;
    @FXML private JFXTextField textFieldName;
    private PreparedStatement ps;
    private ResultSet rs;
    private File file,chooserImage;
    private Image image,defaultImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //create file folder to save school logo
        new File(System.getProperty("user.home")+File.separator+"SchoolLogo").mkdirs();
        
        defaultImage=imageView.getImage();
        
        try {
            ps=SchoolDataBase.getCon().prepareStatement("Select * From School_Details");
            rs=ps.executeQuery();
            
            if(rs.next()){
                file=new File(rs.getString("Logo"));
                BufferedImage read=ImageIO.read(file);
                image=SwingFXUtils.toFXImage(read, null);
                imageView.setImage(image);
                
                textFieldName.setText(rs.getString("Name"));
            }
            ps.close();
        } catch (Exception e) {}
    }    
    
    @FXML
    private void upLoadImage(){
        FileChooser ch=new FileChooser();
        ch.setTitle("Upload School Logo");
        ch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg" ,"*.jepg"));
        chooserImage=ch.showOpenDialog(textFieldName.getScene().getWindow());
        
        if (chooserImage!=null) {
            try {
                image=new Image(chooserImage.toURI().toURL().toString());
                imageView.setImage(image);
            } catch (Exception e) {}
        }
    }
    
    @FXML
    private void saveAction(ActionEvent event) {
        try {
            String name=textFieldName.getText();
            if (name.isEmpty()||imageView.getImage()==defaultImage) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "School Details", "Invalid Input");
            }
            else{
                ps=SchoolDataBase.getCon().prepareStatement("Truncate Table School_Details");
                ps.executeUpdate();
                ps.close();
                
                ps=SchoolDataBase.getCon().prepareStatement("Insert Into School_Details Values(?,?)");

                if (chooserImage!=null) {//if a image is choosen

                    //check if the recent image exist and delete it.
                    //this will avoid deleting the image everytime an image is Choosen
                    if (file==null) {
                        
                    }
                    else if (file.exists()) {
                        file.delete();
                    }
                    
                    InputStream in=new FileInputStream(chooserImage);
                    OutputStream out=new FileOutputStream(System.getProperty("user.home")+File.separator+"SchoolLogo"+File.separator+chooserImage.getName());
                    byte[] data=new byte[1024];
                    int size;
                    while((size=in.read(data))!=-1){
                        out.write(data, 0, size);
                    }
                    out.close();
    
                    ps.setString(1, System.getProperty("user.home")+File.separator+"SchoolLogo"+File.separator+chooserImage.getName());
                }else{//if image is not choosen 
                    //save the same Available Image path to DataBase
                    ps.setString(1, System.getProperty("user.home")+File.separator+"SchoolLogo"+File.separator+file.getName());
                }
                ps.setString(2, name);
                ps.executeUpdate();

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "School Details", "Saved");

                Bridge.schName.setText(name.toUpperCase());
                Bridge.schNameReport.setText(name.toUpperCase());
                Bridge.schLogo.setImage(imageView.getImage());
            }
        } catch (Exception e) {}
    }
}
