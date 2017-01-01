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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
            if (FrameworkConfiguration.Frameworks.XMLRPC.getImplementation().equals(FrameworkConfiguration.getFrameworkImplementation())){
                try {
                    List<String> params = new ArrayList<String>();
                    params.add(loginInput);
                    params.add(passwordInput);
                    String result=( String) XmlRpcAPI.getXmlRpcServer().execute("UserService.login",params);
                    if (ServerStatuses.SUCCESS.name().equals(result)){
                        ChatClientCache.setCurrentUserUsername(loginInput);
                        params.remove(passwordInput);
                        Map<String, String> currentUserProfile=(Map) XmlRpcAPI.getXmlRpcServer().execute("UserService.profile",params);
                        ChatClientCache.setCurrentUserProfile(currentUserProfile);
                        ChatClientCache.setCurrentUserStatus(currentUserProfile.get("userStatus"));
                        params=new ArrayList<String>();
                        params.add(ChatClientCache.getCurrentUserStatus());
                        List<Object> channels= Arrays.asList( (Object[]) XmlRpcAPI.getXmlRpcServer().execute("UserService.channelsByStatus",params));
                        List<String> channelsList =new ArrayList<>();
                        for (Object channel : channels){
                            channelsList.add((String)channel);
                        }
                        ChatClientCache.setChannels(channelsList);
                        root=FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
                        Scene scene=new Scene(root);
                        stage= (Stage)((Node)event.getSource()).getScene().getWindow();
                        stage.setTitle("Super Chat");
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
        Platform.exit();
    }

    @FXML
    public void configureXmlRpc(ActionEvent event) {
        if (event.getSource()==xmlrpc){
            FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.XMLRPC.getImplementation());
        }
    }
    @FXML
    public void configureBurlap(ActionEvent event) {
        if (event.getSource()==burlap){
            FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.BURLAP.getImplementation());
        }
    }

    @FXML
    public void configureHessian(ActionEvent event){
        if (event.getSource()==hessian){
            FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.HESSIAN.getImplementation());
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
