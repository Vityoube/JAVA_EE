package org.vkalashnykov.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.vkalashnykov.configuration.ClientStatuses;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private MenuItem close;

    @FXML
    private MenuItem burlap;

    @FXML
    private MenuItem hessian;

    @FXML
    private MenuItem xmlrpc;

    private  String configuration="xmlrpc";

    private final String serverUrl="http://localhost:8090/xmlrpc";

    private final XmlRpcClient xmlRpcClient=new XmlRpcClient();

    public Controller() throws Exception {
        final XmlRpcClientConfigImpl config=new XmlRpcClientConfigImpl();
        try {
            System.out.println("Trying connect to server "+serverUrl);
            config.setServerURL(new URL(serverUrl));
        } catch (MalformedURLException e) {
            System.err.println("Server "+serverUrl+" not found.");
            e.printStackTrace();
            errorLabel.setText("Server not found.");
            throw new Exception("Server not found.");
        }
        xmlRpcClient.setConfig(config);
        System.out.println("Connected to server: "+serverUrl);
    }

    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    public void loginAction(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        if (login.getText()!= null && password.getText()!=null){
            if ("xmlrpc".equals(configuration)){
                try {
                    List<String> params = new ArrayList<String>();
                    params.add(login.getText());
                    params.add(password.getText());
                    String result=( String)xmlRpcClient.execute("UserService.login",params);
                    if (ClientStatuses.SUCCESS.name().equals(result)){
                        stage=(Stage)loginButton.getScene().getWindow();
                        root= FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }
                    else{
                        throw new XmlRpcException(404,"Bad credentials");
                    }

                } catch (XmlRpcException e) {
                   errorLabel.setText("Error on server: "+serverUrl+ ". Error code: "+e.code+" "+e.getMessage());
                }
            }

        } else {
            errorLabel.setText("Username and password cannot be empty!");
        }
    }

    @FXML
    public void closeAction(ActionEvent event) {
        if (event.getSource()==close){
            Platform.exit();
        }
    }

    @FXML
    public void configureXmlRpc(ActionEvent event) {
        if (event.getSource()==xmlrpc){
            configuration="xmlrpc";
        }
    }
    @FXML
    public void configureBurlap(ActionEvent event) {
        if (event.getSource()==burlap){
            configuration="burlap";
        }
    }

    @FXML
    public void configureHessian(ActionEvent event){
        if (event.getSource()==hessian){
            configuration="hessian";
        }
    }
}
