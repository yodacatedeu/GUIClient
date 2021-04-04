/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Aron
 */
public class LoginController implements Initializable {
        
    @FXML
    private Label lblStatus;
    
    @FXML
    private TextField txtServerIP;
    
    @FXML
    private TextField txtPort;
    
    @FXML
    private TextField txtPassword;
    
//    @FXML
//    private Button loginButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void connect(String ip, int port, String password, ActionEvent event) {
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Main.setSock(new Socket(ip,port));
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        lblStatus.setTextFill(Color.web("orange"));
                        lblStatus.setText("Failed to connect.  Please confrim credentials then try again.");
                    });
                    return null;
                }
                // successfull connection
                InputStream in;
                String line;
                try {

                    in = Main.getSock().getInputStream();

                    BufferedReader bin = new
                        BufferedReader(new InputStreamReader(in));
                    // Check if blacklisted or Just to ignore "Please enter password prompt from console client)
                    line = Client.decrypt(bin.readLine());
                    if (line.startsWith("You are currently blacklisted")) {
                        final String temp = line;
                        Platform.runLater(() -> {
                            lblStatus.setTextFill(Color.web("#dc143c"));
                            lblStatus.setText(temp + "  Please exit.");
                        });
                    }
                    else {
                        // Send Password
                        PrintWriter pout;
                        pout = new PrintWriter(Main.getSock().getOutputStream(), true);
                        pout.println(Client.encrypt(password));

                        // read repsonse to inputted password
                        if ((line = Client.decrypt(bin.readLine())) != null && !line.startsWith("Please enter a name: ")) {
                            //System.out.println(line);
                            final String temp = line;
                            Platform.runLater(() -> {
                                lblStatus.setTextFill(Color.web("#dc143c"));
                                lblStatus.setText("Incorrect password please try again.");
                            });
                            Main.getSock().close();
                        }

                        // They have logged in, start main client
                        else if (line != null) {
                            final String temp = line;
                            Platform.runLater(() -> {
                                try {
                                    lblStatus.setTextFill(Color.web("#2e8b57"));
                                    lblStatus.setText("You successfully connected.");
                                    //loginButton.disableProperty().setValue(Boolean.TRUE);
                                    ((Node)event.getSource()).getScene().getWindow().hide();
                                    Stage stage = new Stage();
                                    FXMLLoader loader = new FXMLLoader();
                                    Pane root = loader.load(getClass().getResource("Main.fxml").openStream());
                                    ClientController clientController = (ClientController)loader.getController();
                                    Scene scene = new Scene(root);
                                    //scene.getStylesheets().add(getClass().getResource("application.CSS").toExternalForm());
                                    stage.setScene(scene);
                                    stage.setOnCloseRequest((WindowEvent event1) -> {
                                        pout.println(Client.encrypt("exit"));
                                        try {
                                            Main.getSock().close();
                                        } catch (IOException ex) {
                                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        Platform.exit();
                                        System.exit(0);
                                    });
                                    stage.getIcons().add(new Image("img/lemon.png"));
                                    stage.setTitle("The Dropoff");
                                    stage.show();
                                    clientController.updateScreen(temp);
                                    clientController.handleServerInput(bin);                                  
                                    //System.out.println("end of login box thread");
                                } catch (IOException ex) {
                                    System.err.println(ex);
                                }
                            });
                        }
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                    //System.err.println("Issue in recieving from server.");
                    //lblStatus.setTextFill(Color.web("orange"));
                    //lblStatus.setText("Failed to connect to server.  Please confrim credentials and try again.");
                } 
                return null;
            }    
        };
        new Thread(task).start(); 
    }
    
    public void Login(ActionEvent event) throws InterruptedException {
//        System.out.println("loginBtnCLicked was " + loginBtnClicked);
//        loginBtnClicked = true;
//        System.out.println("loginBtnClicked set to " + loginBtnClicked);
        //Platform.runLater(() -> {
                      
        //});
        lblStatus.setTextFill(Color.web("yellow"));
        lblStatus.setText("Attempting to connect to server...");   
        //System.out.println("Connecting.");
        
        String ip = txtServerIP.getText();
        String password = txtPassword.getText();
        Integer port = Integer.parseInt(txtPort.getText());
        // Make connection to server

        // Server IP
        //Socket sock = new Socket("192.168.1.129",80);
        // Router IP
        //Socket sock = new Socket("142.129.175.110",80);
        connect(ip, port, password, event);            
    }

}
