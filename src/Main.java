/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Aron
 */
public class Main extends Application {
    
    private static Socket sock;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml")); 
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("application.CSS").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("The Mad Dash");
        stage.getIcons().add(new Image("img/LemonMan.jpg"));
        stage.setOnCloseRequest((WindowEvent event1) -> {                      
            Platform.exit();                            
            System.exit(0);
        });
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the sock
     */
    public static Socket getSock() {
        return sock;
    }

    /**
     * @param aSock the sock to set
     */
    public static void setSock(Socket aSock) {
        sock = aSock;
    }
    
}
