package org.vkalashnykov.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.api.ChatClientApi;
import org.vkalashnykov.api.XmlRpcAPI;
import org.vkalashnykov.configuration.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by vkalashnykov on 28.12.16.
 */
public class ChatMainController implements Initializable{
    public Label startCurrentUsername;
    public ListView channelsList;
    public Label channel;
    public TextField searchChannel;
    public Button sendButton;
    public TextArea messageArea;
    public Hyperlink channelDetailsHyperlink;
    public TextArea messagesWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startCurrentUsername.setText(ChatClientCache.getCurrentUserUsername());
        updateStatus();
        onStatusChange();
        setChannelListItemsListener();
        listenToUserServerStatus();
        updateMessagesOnChannel();
    }

    private void updateMessagesOnChannel() {
        Timeline updateMessages=new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!ChatClientCache.getCurrentChannel().isEmpty()){
                    try {
                        List<Object> messagesOnChannel=
                                ChatClientApi.messagesOnChannel(ChatClientCache.getCurrentChannel(),ChatClientCache.getCurrentUserUsername());
                        for (Object messageOnChannel: messagesOnChannel)
                            if (!messagesWindow.getText().contains((String)messageOnChannel))
                            messagesWindow.appendText((String)messageOnChannel);
                    } catch (XmlRpcException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
        updateMessages.setCycleCount(Animation.INDEFINITE);
        updateMessages.play();

    }

    private void updateStatus() {
        Timeline getUserStatus = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    String currentUserStatus=ChatClientApi.getUserStatus();
                    ChatClientCache.setCurrentUserStatus(currentUserStatus);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        }));
        getUserStatus.setCycleCount(Animation.INDEFINITE);
        getUserStatus.play();

    }


    public void onClose(ActionEvent event) {

        try {
           String logout=ChatClientApi.logout();
            if (ServerStatuses.SUCCESS.name().equals(logout)) {
                ChatClientCache.cleanCache();
                Platform.exit();
            }
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

    }

    public void onLogout(ActionEvent event) {
        Stage stage;
        Parent root;
        try {
            String logout=ChatClientApi.logout();
            if (ServerStatuses.SUCCESS.name().equals(logout)){
                ChatClientCache.cleanCache();
                root= FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                Scene scene=new Scene(root);
                stage= (Stage)((Node)startCurrentUsername).getScene().getWindow();
                stage.setTitle("Login");
                stage.setScene(scene);
                stage.show();
            }
        } catch (XmlRpcException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCurrentUserProfile(ActionEvent event) {
        Stage stage;
        Parent root;
        List<String> params=new ArrayList<>();
        params.add(ChatClientCache.getCurrentUserUsername());
        try{
            ChatClientCache.setUsername(ChatClientCache.getCurrentUserUsername());
            ChatClientCache.setUserProfile(ChatClientCache.getCurrentUserProfile());
            ChatClientCache.setProfileUserStatus(ChatClientCache.getCurrentUserStatus());
            root=FXMLLoader.load(getClass().getResource("/fxml/profile.fxml"));
            Scene scene=new Scene(root);
            stage=new Stage();
            stage.setScene(scene);
            stage.setTitle(ChatClientCache.getCurrentUserUsername()+"'s profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSearch(KeyEvent keyEvent){
        String searchedChannel=searchChannel.getText();
        if (searchedChannel.isEmpty()){
            channelsList.setItems((FXCollections.observableList(ChatClientCache.getChannels())));
        }
        else if (ChatClientCache.getChannels().contains(searchedChannel)){
            List<String> channel=new ArrayList();
            channel.add(searchedChannel);
            channelsList.setItems(FXCollections.observableList(channel));
        } else {
            List<String> channels=new ArrayList<>();
            for (String channel : ChatClientCache.getChannels()){
                if (channel.contains(searchedChannel))
                    channels.add(channel);
            }
            channelsList.setItems(FXCollections.observableList(channels));
        }

    }

    public void channelDetails() throws XmlRpcException, IOException {
        ChatClientCache.setChannelDetails(ChatClientApi.channelDetails());
        Parent root =FXMLLoader.load(getClass().getResource("/fxml/channel_details.fxml"));
        Scene scene = new Scene(root);
        Stage stage=new Stage();
        stage.setTitle("Channel "+ChatClientCache.getCurrentChannel()+" details");
        stage.setScene(scene);
        stage.show();

    }

    public void onXmlRpc(ActionEvent event) {
        FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.XMLRPC.getImplementation());
    }

    public void onBurlap(ActionEvent event) {
        FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.BURLAP.getImplementation());
    }

    public void onHessian(ActionEvent event) {
        FrameworkConfiguration.setFrameworkImplementation(FrameworkConfiguration.Frameworks.HESSIAN.getImplementation());
    }

    public void listenToUserServerStatus(){
        Timeline userServerStatusListener=new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    String result=ChatClientApi.checkUserServerStatus();
                    if (UserServerStatus.BAN.getUserServerStatus().equals(result)) {
                        Platform.exit();
                        Parent root=FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
                        Scene scene=new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("login");
                        stage.setResizable(false);
                    }
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
        userServerStatusListener.setCycleCount(Animation.INDEFINITE);
        userServerStatusListener.play();
    }

    public void onStatusChange(){
        Timeline updateChannelsByStatus=new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    List<Object> channels=ChatClientApi.channelsByStatus();
                    List<String> listOfChannels =new ArrayList<>();
                    for (Object channel : channels){
                        listOfChannels.add((String)channel);
                    }
                    ChatClientCache.setChannels(listOfChannels);
                    channelsList.setItems(FXCollections.observableList(ChatClientCache.getChannels()));
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }

            }
        }));
        updateChannelsByStatus.setCycleCount(Animation.INDEFINITE);
        updateChannelsByStatus.play();
    }

    public void setChannelListItemsListener(){
        channelsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                messagesWindow.setText("");
                String selectedChannel=(String)channelsList.getSelectionModel().getSelectedItem();
                channel.setText(selectedChannel);
                channel.setVisible(true);
                sendButton.setDisable(false);
                messageArea.setEditable(true);
                channelDetailsHyperlink.setDisable(false);
                channelDetailsHyperlink.setVisible(true);
                ChatClientCache.setCurrentChannel(selectedChannel);
                try {
                    Map<String,String> channelDetails=ChatClientApi.enterChannel();
                    ChatClientCache.setChannelDetails(channelDetails);
                    List<Object> usersOnChannel =ChatClientApi.usersOnChannel();
                    List<String>usersOnChannelList=new ArrayList<String>();
                    for(Object userOnChannel : usersOnChannel)
                        usersOnChannelList.add((String)userOnChannel);
                    ChatClientCache.setUsersOnChannel(usersOnChannelList);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onSendMessage(ActionEvent event) {
        try {
            String messageText = messageArea.getText();
            String result=ChatClientApi.sendMessage(messageText);
            messageArea.setText("");
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

    }
}
