
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Aron
 */
public class ClientController implements Initializable {

    @FXML
    private Label mainLabel;
    
    @FXML
    private TextArea msgInputBox;
    
    @FXML
    //private TextFlow displayScreen;
    private TextArea displayScreen;
    
//    @FXML
//    private ScrollPane screenScroll;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void sendMessage(ActionEvent event) throws IOException {
        PrintWriter pout;
        pout = new PrintWriter(Main.getSock().getOutputStream(), true);
        pout.println(Client.encrypt(msgInputBox.getText()));
        //Text msg = new Text(msgInputBox.getText()+"\n");
        //msg.setFill(Color.web("#40e0d0"));
        //displayScreen.getChildren().add(msg);
        displayScreen.appendText(msgInputBox.getText()+"\n");
        //screenScroll.autosize();
        msgInputBox.setText("");
        //displayScreen.autosize();
    }
    
    public void disconnect(ActionEvent event) throws IOException {
        PrintWriter pout;
        pout = new PrintWriter(Main.getSock().getOutputStream(), true);
        pout.println(Client.encrypt("EXIT"));
        Main.getSock().close();
        //displayScreen.getChildren().add(new Text("You have disconnected from the server\n"));
        displayScreen.appendText("You have disconnected from the server\n");
        
        ((Node)event.getSource()).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("Login.fxml").openStream()); 
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("application.CSS").toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(new Image("img/LemonMan.jpg"));
        stage.setTitle("The Mad Dash");
        stage.setOnCloseRequest((WindowEvent event1) -> {                      
            Platform.exit();                            
            System.exit(0);
        });
        stage.show();
    }
    
    // Is only ever to be called once per client login
    public void handleServerInput(BufferedReader nis) throws IOException {
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String output;
                while((output = Client.decrypt(nis.readLine())) != null) {
                    final String value = output;
                    Platform.runLater(() -> {
                        displayScreen.appendText(value + System.getProperty("line.separator"));           
                    });
                }
                Platform.runLater(() -> {
                    displayScreen.appendText("You have been kicked from the server." + System.getProperty("line.separator"));           
                });
                return null;
            }    
        };
        new Thread(task).start();
    }
    
    public void updateScreen(String msg) {
        //Text text = new Text("\n"+msg);
        //displayScreen.getChildren().add(text);
        displayScreen.appendText(msg+"\n");
    }
    
}
    
    
  
