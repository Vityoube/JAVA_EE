package org.vkalashnykov.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.api.ChatClientApi;
import org.vkalashnykov.configuration.ChatClientCache;
import org.vkalashnykov.api.XmlRpcAPI;

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
    public TextFlow userFlow;

    private List<String> usersOnChannelList;
    private List<Hyperlink> linkList =new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Timeline usersUpdater=new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    userFlow.getChildren().removeAll(linkList);
                    Map<String,String> channelDetails= ChatClientApi.channelDetails();
                    ChatClientCache.setChannelDetails(channelDetails);
                    channelName.setText(ChatClientCache.getCurrentChannel());
                    channelStatus.setText(ChatClientCache.getChannelDetails().get("allowedStatus"));
                    channelDescription.setText(ChatClientCache.getChannelDetails().get("description"));
                    usersCount.setText(ChatClientCache.getChannelDetails().get("usersCount"));
                    List<Object> usersOnChannel =ChatClientApi.usersOnChannel();
                    usersOnChannelList=new ArrayList<>();
                    for(Object userOnChannel : usersOnChannel)
                        usersOnChannelList.add((String)userOnChannel);
                    if (!ChatClientCache.getUsersOnChannel().equals(usersOnChannelList)){
                        ChatClientCache.setUsersOnChannel(usersOnChannelList);
                    }
                    linkList=new ArrayList<>();
                    for (String user : ChatClientCache.getUsersOnChannel())
                        linkList.add(new Hyperlink(user));
                    for (Hyperlink link : linkList)
                        userFlow.getChildren().add(link);
                    ListIterator<Hyperlink> hyperlinkIterator=linkList.listIterator();
                    while (hyperlinkIterator.hasNext()) {
                        Hyperlink link = (Hyperlink) hyperlinkIterator.next();
                        link.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try {
                                    List<String> params = new ArrayList<String>();
                                    String userProfileName = link.getText();
                                    Map<String, String> userProfile = ChatClientApi.profile(userProfileName);
                                    ChatClientCache.setUserProfile(userProfile);
                                    ChatClientCache.setUsername(userProfileName);
                                    ChatClientCache.setProfileUserStatus(userProfile.get("userStatus"));
                                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/profile.fxml"));
                                    Stage stage = new Stage();
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.setTitle(link.getText() + "'s profile");
                                    stage.show();
                                } catch (XmlRpcException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }

            }
        }));
        usersUpdater.setCycleCount(Timeline.INDEFINITE);
        usersUpdater.play();

    }


}
