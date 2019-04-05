/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentdetails;

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
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class EditStudentController implements Initializable {

    private PreparedStatement ps;
    private ResultSet res;
    @FXML
    private JFXComboBox combo;
    @FXML
    private ImageView image;
    @FXML
    private JFXTextField name, pName, address, pContact;
    @FXML
    private JFXRadioButton m, f;
    @FXML
    private JFXDatePicker date;
    @FXML
    private ToggleGroup tg;
    private String selectedName;
    private File chooserImage, file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Force the text field to numeric only
        pContact.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                pContact.setText(newValue.replaceAll("[^\\d]", ""));
                Toolkit.getDefaultToolkit().beep();
            }
        });

        //Force the text field to letters only
        name.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                name.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });

        //Force the text field to letters only
        pName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                pName.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });
    }

    //fill combo box with Classes
    public void comboValues(ObservableList classes) {
        combo.setItems(classes);
    }

    //get selected name from ListView on Student Class Controller
    public void selectedName(String selectedName) {
        this.selectedName = selectedName;
        try {
            ps = SchoolDataBase.getCon().prepareStatement("select * from StudentInfo where name=?");
            ps.setString(1, selectedName);
            res = ps.executeQuery();

            while (res.next()) {
                name.setText(res.getString("name"));

                if (res.getString("sex").contentEquals("M")) {
                    m.setSelected(true);
                } else {
                    f.setSelected(true);
                }
                date.setValue(res.getDate("age").toLocalDate());

                file = new File(res.getString("photo"));
                BufferedImage read = ImageIO.read(file);
                Image photo = SwingFXUtils.toFXImage(read, null);
                image.setImage(photo);

                combo.setValue(res.getString("class"));
                pName.setText(res.getString("parent"));
                address.setText(res.getString("address"));
                pContact.setText(res.getString("number"));
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void imageAction() {
        //file chosser object
        FileChooser ch = new FileChooser();
        ch.setTitle("Add Photo");

        //set filter to know the specific extention
        ch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jepg"));

        //use the open dialog becaues file needs to be shown/opened
        //assign it to a file variable
        chooserImage = ch.showOpenDialog(combo.getScene().getWindow());

        //this if is to avoid a Null Pointer Exception
        if (chooserImage != null) {
            try {
                Image im = new Image(chooserImage.toURI().toURL().toString());
                image.setImage(im);
            } catch (Exception e) {
            }
        }
    }

    /*
    How to replace a Image File in a DataBase
    Delete the exiting Image from the Folder you saved the actual image
    get the new File chosser image
    save the actual image to the Folder
    then save iamge file path to Database
     */
    @FXML
    private void saveAction() {
        String sName = name.getText().trim();
        String parentName = pName.getText().trim();
        String parentCon = pContact.getText().trim();
        String sAddress = address.getText().trim();

        if (image.getImage() == null || sName.isEmpty() || parentName.isEmpty() || parentCon.isEmpty() || sAddress.isEmpty() || combo == null || date.getValue() == null || tg.getSelectedToggle() == null) {
            SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Edit Student", "Invalid Input");
        } else {
            try {
                ps = SchoolDataBase.getCon().prepareStatement("update StudentInfo set name=?,sex=?,age=?,photo=?,class=?,parent=?,address=?,number=? where name=?");
                ps.setString(1, sName);
                if (m.isSelected()) {
                    ps.setString(2, "M");
                } else {
                    ps.setString(2, "F");
                }
                ps.setString(3, date.getValue().toString());

                //save File Chooser Image to file
                //than save file Path Name to Database
                if (chooserImage != null) {//if a image is choosen

                    //check if the recent image exist and delete it.
                    //this will avoid deleting the image everytime an image is Choosen
                    if (file.exists()) {
                        file.delete();//Delete the Exiting Student Photo
                    }

                    InputStream in = new FileInputStream(chooserImage);
                    OutputStream out = new FileOutputStream(System.getProperty("user.home") + File.separator + "StudentdbPhotos" + File.separator + chooserImage.getName());
                    byte[] data = new byte[1024];
                    int size;
                    while ((size = in.read(data)) != -1) {//-1 means if the end of the stream is reached
                        out.write(data, 0, size);
                    }
                    out.close();

                    ps.setString(4, System.getProperty("user.home") + File.separator + "StudentdbPhotos" + File.separator + chooserImage.getName());
                } else {//if image is not choosen 
                    //save the same Available Image path to DataBase
                    //this will avoid an sql error (Photo=?)
                    ps.setString(4, System.getProperty("user.home") + File.separator + "StudentdbPhotos" + File.separator + file.getName());
                }

                ps.setString(5, combo.getValue().toString());
                ps.setString(6, parentName);
                ps.setString(7, sAddress);
                ps.setString(8, parentCon);
                ps.setString(9, selectedName);
                ps.executeUpdate();

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Edit Student", "Change Succssful");

                name.getScene().getWindow().hide();
            } catch (Exception e) {
            }
        }
    }
}
