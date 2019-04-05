/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpreloader;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author Andre Kelvin
 */
public class SchoolPreloader extends Preloader {
    
    //ProgressBar bar;
    Stage stage;
    
//    private Scene createPreloaderScene() {
//        bar = new ProgressBar();
//        BorderPane p = new BorderPane();
//        p.setCenter(bar);
//        return new Scene(p, 300, 150);        
//    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("SchoolPreloader.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("schoolhouse-icon.png")));
        stage.show();
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        System.out.println("State: "+scn.getType());
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
    
//    @Override
//    public void handleProgressNotification(ProgressNotification pn) {
//        bar.setProgress(pn.getProgress());
//    }    
    
}
