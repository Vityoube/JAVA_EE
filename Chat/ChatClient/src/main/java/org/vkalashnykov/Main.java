package org.vkalashnykov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.vkalashnykov.configuration.XmlRpcConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        XmlRpcConfiguration.configureXmlRpcClient();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
