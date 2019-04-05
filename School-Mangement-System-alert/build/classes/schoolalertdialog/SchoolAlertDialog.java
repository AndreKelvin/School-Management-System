/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolalertdialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolAlertDialog {

    private static Alert a;
    
    public static void showAlert(Alert.AlertType alertType, String title, String msg){
        a=new Alert(alertType);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
    
    public static void showDeleteAlert(Alert.AlertType alertType, String title, String msg){
        a=new Alert(alertType);
        a.setTitle(title);
        a.setContentText(msg);    }
    
    public static Alert alertObject() {
        return a;
    }
    
    public static Optional<ButtonType> showAndwait(){
        return a.showAndWait();
    }
}
