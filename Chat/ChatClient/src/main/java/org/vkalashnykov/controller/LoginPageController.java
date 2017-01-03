package org.vkalashnykov.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.api.ChatClientApi;
import org.vkalashnykov.api.XmlRpcAPI;
import org.vkalashnykov.configuration.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class LoginPageController implements Initializable {
    private double xOffset=0;
    private double yOffset=0;

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
                try {
                    String result=(String) ChatClientApi.login(loginInput,passwordInput);
                    if (ServerStatuses.SUCCESS.name().equals(result)){
                        ChatClientCache.setCurrentUserUsername(loginInput);
                       Map<String,String> currentUserProfile= ChatClientApi.profile(ChatClientCache.getCurrentUserUsername());
                        ChatClientCache.setCurrentUserProfile(currentUserProfile);
                        ChatClientCache.setCurrentUserStatus(currentUserProfile.get("userStatus"));
                        List<Object> channels=ChatClientApi.channelsByStatus();
                        List<String> channelsList =new ArrayList<>();
                        for (Object channel : channels){
                            channelsList.add((String)channel);
                        }
                        ChatClientCache.setChannels(channelsList);
                        root=FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));

                        Scene scene=new Scene(root);
                        stage= (Stage)((Node)event.getSource()).getScene().getWindow();
                        root.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                xOffset=event.getSceneX();
                                yOffset=event.getSceneY();
                            }
                        });
                        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                stage.setX(event.getScreenX()-xOffset);
                                stage.setY(event.getScreenY()-yOffset);
                            }
                        });
                        stage.setTitle("Super Chat");
                        stage.setScene(scene);
                        stage.show();
                    }
                    else{
                        throw new XmlRpcException(404,result);
                    }

                } catch (XmlRpcException e) {
                   errorLabel.setText(e.getMessage());
                }

        } else {
            errorLabel.setText(ErrorCodes.EMPTY_PASSWORD_OR_USERNAME.getErrorDescription());
        }
    }

    @FXML
    public void closeAction(ActionEvent event) {
        Platform.exit();
    }


    public void configureXmlRpc(ActionEvent event) {
        if (event.getSource()==xmlrpc){
            FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.XMLRPC.getImplementation());
        }
    }
    public void configureBurlap(ActionEvent event) {
        if (event.getSource()==burlap){
            FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.BURLAP.getImplementation());
        }
    }


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
