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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import schoolalertdialog.SchoolAlertDialog;
import schooldatabase.SchoolDataBase;

/**
 * FXML Controller class
 *
 * @author Andre Kelvin
 */
public class AddStudentController implements Initializable {

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
    private File file;
    private Image defaultImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        defaultImage = image.getImage();
        image.setImage(defaultImage);

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

    @FXML
    private void imageAction() {
        FileChooser ch = new FileChooser();
        ch.setTitle("Choose Photo");
        ch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jepg"));

        file = ch.showOpenDialog(combo.getScene().getWindow());

        if (file != null) {
            try {
                Image im = new Image(file.toURI().toURL().toString());
                image.setImage(im);
            } catch (Exception e) {
            }
        }
    }

    @FXML
    private void addAction() {
        try {
            String sName = name.getText().trim();
            String parentName = pName.getText().trim();
            String parentCon = pContact.getText().trim();
            String sAddress = address.getText().trim();

            if (sName.isEmpty() || parentName.isEmpty() || parentCon.isEmpty() || sAddress.isEmpty() || combo == null || date.getValue() == null || tg.getSelectedToggle() == null || image.getImage() == defaultImage) {
                SchoolAlertDialog.showAlert(Alert.AlertType.ERROR, "Add Student", "Invalid Input");
            } else {
                //save image to file
                //than save file Path to Database
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(System.getProperty("user.home") + File.separator + "StudentdbPhotos" + File.separator + file.getName());
                byte[] data = new byte[1024];
                int size;
                while ((size = in.read(data)) != -1) {//-1 means if the end of the stream is reached
                    out.write(data, 0, size);
                }
                out.close();

                PreparedStatement ps = SchoolDataBase.getCon().prepareStatement("insert into StudentInfo (Name,Sex,Age,Photo,Class,Parent,Address,Number) values(?,?,?,?,?,?,?,?)");
                ps.setString(1, sName);
                if (m.isSelected()) {
                    ps.setString(2, "M");
                } else {
                    ps.setString(2, "F");
                }
                ps.setString(3, date.getValue().toString());
                ps.setString(4, System.getProperty("user.home") + File.separator + "StudentdbPhotos" + File.separator + new File(file.getName()));
                ps.setString(5, combo.getValue().toString());
                ps.setString(6, parentName);
                ps.setString(7, sAddress);
                ps.setString(8, parentCon);
                ps.executeUpdate();
                ps.close();

                SchoolAlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Add Student", "Added");

                name.clear();
                pName.clear();
                pContact.clear();
                address.clear();
                m.setSelected(false);
                f.setSelected(false);
                date.setValue(null);
                combo.setValue(null);
                image.setImage(defaultImage);

                //Pass the total number of Student,Boys,Grils added
                ps = SchoolDataBase.getCon().prepareStatement("Select Count(Name) From STUDENTINFO");
                ResultSet res = ps.executeQuery();
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
            }
        } catch (Exception e) {
        }
    }
}
