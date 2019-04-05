/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schooldatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Andre Kelvin
 */
public class SchoolDataBase {

    private static Connection con;
    
    public static void initDB() {
        try {
            
            System.setProperty("derby.system.home", System.getProperty("user.home")+File.separator+"SchoolDB");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            
            con=DriverManager.getConnection("jdbc:derby:mydatabase");
        } catch (Exception e) {}
    }
    
    public static Connection getCon(){
        return con;
    }
    
    public static void closeDB(){
        try {
            con.close();
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (Exception e) {}
    }
    
}
