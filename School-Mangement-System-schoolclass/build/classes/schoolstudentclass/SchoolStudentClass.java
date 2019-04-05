/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstudentclass;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schooldatabase.SchoolDataBase;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolStudentClass extends Application {

    private Parent root;

    @Override
    public void start(Stage stage) throws Exception {
        //schooldatabase.SchoolDataBase.initDB();
        //root = FXMLLoader.load(getClass().getResource("SchoolStudentClass.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public SchoolStudentClass() {
        try {
            SchoolDataBase.initDB();
            root = FXMLLoader.load(getClass().getResource("SchoolStudentClass.fxml"));

        } catch (Exception e) {}
    }
    
    public Parent getFxml() {
            return root;
        }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}