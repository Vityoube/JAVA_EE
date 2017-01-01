package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.ChatClientCache;
import org.vkalashnykov.configuration.XmlRpcAPI;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public class ChannelController implements Initializable{

    public Label channelName;
    public Label channelStatus;
    public TextArea channelDescription;
    public Label usersCount;
    public AnchorPane usersPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        channelName.setText(ChatClientCache.getCurrentChannel());
        channelStatus.setText(ChatClientCache.getChannelDetails().get("allowedStatus"));
        channelDescription.setText(ChatClientCache.getChannelDetails().get("description"));
        usersCount.setText(ChatClientCache.getChannelDetails().get("usersCount"));
        ListIterator<String> usersIterator=ChatClientCache.getUsersOnChannel().listIterator();
        HBox usersBox= new HBox();
        while(usersIterator.hasNext()) {
            String user = usersIterator.next();
            Hyperlink userLink = new Hyperlink(user);
            usersBox.getChildren().add(userLink);
            usersBox.setSpacing(3);
        }
        usersPane.getChildren().add(usersBox);
        Iterator<Node> linkIterator=usersBox.getChildren().iterator();
        while(linkIterator.hasNext()){
            Hyperlink link=(Hyperlink)linkIterator.next();
            link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{
                        List<String> params=new ArrayList<String>();
                        String userProfileName=link.getText();
                        params.add(userProfileName);
                        Map<String,String> userProfile = (Map<String,String>) XmlRpcAPI.getXmlRpcServer().execute("UserService.profile",params);
                        ChatClientCache.setUserProfile(userProfile);
                        ChatClientCache.setUsername(userProfileName);
                        Parent root= FXMLLoader.load(getClass().getResource("/fxml/profile.fxml"));
                        Stage stage=new Stage();
                        Scene scene=new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle(link.getText()+"'s profile");
                        stage.show();
                    } catch (XmlRpcException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
