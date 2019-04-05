/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolstart;

import com.sun.javafx.application.LauncherImpl;
import schoolpreloader.SchoolPreloader;
import student.management.system.MainFrame;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolStart {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainFrame.class, SchoolPreloader.class, args);
    }
    
}
