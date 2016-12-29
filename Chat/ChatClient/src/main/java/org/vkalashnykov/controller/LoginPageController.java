package org.vkalashnykov.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.ErrorCodes;
import org.vkalashnykov.configuration.ServerStatuses;
import org.vkalashnykov.configuration.XmlRpcConfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

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

    @FXML
    private Pane main;

    @FXML
    private Pane loginTab;

    @FXML
    private TitledPane mainTab;


    private  String configuration="xmlrpc";



    public LoginPageController() throws Exception {

    }

    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    public void loginAction(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        String loginInput=login.getText();
        String passwordInput=password.getText();
        if (loginInput!= null && passwordInput!=null){
            if ("xmlrpc".equals(configuration)){
                try {
                    List<String> params = new ArrayList<String>();
                    params.add(loginInput);
                    params.add(passwordInput);
                    String result=( String) XmlRpcConfiguration.getXmlRpcServer().execute("UserService.login",params);
                    if (ServerStatuses.SUCCESS.name().equals(result)){
                        root=FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
                        Scene scene=new Scene(root);
                        stage= (Stage)((Node)event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    }
                    else{
                        throw new XmlRpcException(404,"Bad credentials");
                    }

                } catch (XmlRpcException e) {
                   errorLabel.setText(e.getMessage());
                }
            }

        } else {
            errorLabel.setText(ErrorCodes.EMPTY_PASSWORD_OR_USERNAME.getErrorDescription());
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

    @FXML
    public void onEnterKey(KeyEvent keyEvent) throws IOException{
        if (KeyCode.ENTER.equals(keyEvent.getCode())){
            Stage stage;
            Parent root;
            String loginInput=login.getText();
            String passwordInput=password.getText();
            if (!"".equals(loginInput) && !"".equals(passwordInput)){
                if ("xmlrpc".equals(configuration)){
                    try {
                        List<String> params = new ArrayList<String>();
                        params.add(loginInput);
                        params.add(passwordInput);
                        String result=( String) XmlRpcConfiguration.getXmlRpcServer().execute("UserService.login",params);
                        if (ServerStatuses.SUCCESS.name().equals(result)){
                            root=FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
                            Scene scene=new Scene(root);
                            stage= (Stage)((Node)keyEvent.getSource()).getScene().getWindow();
                            stage.setTitle("Super Chat");
                            stage.setScene(scene);
                            stage.show();
                        }
                        else{
                            errorLabel.setText("Bad credentials");
                        }

                    } catch (XmlRpcException e) {
                            errorLabel.setText(e.getMessage());
                    }
                }

            } else {
                errorLabel.setText(ErrorCodes.EMPTY_PASSWORD_OR_USERNAME.getErrorDescription());
            }
        }
    }

    @FXML
    public void registerAction(ActionEvent event) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
        Stage stage=new Stage();
        Scene scene=new Scene(root);
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.show();
    }
}
