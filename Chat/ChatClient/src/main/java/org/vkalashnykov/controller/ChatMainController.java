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
import javafx.util.Pair;
import org.apache.xmlrpc.XmlRpcException;
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
                    messagesWindow.setText("");
                    try {
                        List<String> channelParams=new ArrayList<>();
                        channelParams.add(ChatClientCache.getCurrentChannel());
                        channelParams.add(ChatClientCache.getCurrentUserUsername());
                        List<Object> messagesOnChannel=Arrays.asList((Object[])XmlRpcAPI.getXmlRpcServer().execute("UserService.messagesOnChannel",channelParams));
                        for (Object messageOnChannel: messagesOnChannel)
                            messagesWindow.setText(messagesWindow.getText()+(String)messageOnChannel);
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
                    List<String> username=new ArrayList<>();
                    username.add(ChatClientCache.getCurrentUserUsername());
                    String currentUserStatus=(String)XmlRpcAPI.getXmlRpcServer().execute("UserService.getUserStatus",username);
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
            List<String> params=new ArrayList<>();
            params.add(ChatClientCache.getCurrentUserUsername());
            String result =(String) XmlRpcAPI.getXmlRpcServer().execute("UserService.logout",params);

            if (ServerStatuses.SUCCESS.name().equals(result)) {
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
            List<String> params=new ArrayList<>();
            params.add(ChatClientCache.getCurrentUserUsername());
            String result =(String) XmlRpcAPI.getXmlRpcServer().execute("UserService.logout",params);
            if (ServerStatuses.SUCCESS.name().equals(result)){
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
        List<String> params = new ArrayList<>();
        params.add(ChatClientCache.getCurrentChannel());
        Map<String,String> channelDetails=(Map) XmlRpcAPI.getXmlRpcServer().execute("UserService.channelDetails",params);
        ChatClientCache.setChannelDetails(channelDetails);
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
                List<String> userParams=new ArrayList<>();
                userParams.add(ChatClientCache.getCurrentUserUsername());
                try {
                    String result=(String)XmlRpcAPI.getXmlRpcServer().execute("UserService.checkUserServerStatus",userParams);
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
                    List<String> params=new ArrayList<String>();
                    params.add(ChatClientCache.getCurrentUserStatus());
                    List<Object> channels = Arrays.asList( (Object[]) XmlRpcAPI.getXmlRpcServer().execute("UserService.channelsByStatus",params));
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
                String selectedChannel=(String)channelsList.getSelectionModel().getSelectedItem();
                channel.setText(selectedChannel);
                channel.setVisible(true);
                sendButton.setDisable(false);
                messageArea.setEditable(true);
                channelDetailsHyperlink.setDisable(false);
                channelDetailsHyperlink.setVisible(true);
                ChatClientCache.setCurrentChannel(selectedChannel);
                try {
                    List<String> params =new ArrayList<String>();
                    params.add(ChatClientCache.getCurrentUserUsername());
                    params.add(ChatClientCache.getCurrentChannel());
                    Map<String,String> channelDetails = (Map<String,String>) XmlRpcAPI.getXmlRpcServer().execute("UserService.enterChannel",params);
                    ChatClientCache.setChannelDetails(channelDetails);
                    params.remove(ChatClientCache.getCurrentUserUsername());
                    List<Object> usersOnChannel =Arrays.asList((Object[])XmlRpcAPI.getXmlRpcServer().execute("UserService.usersOnChannel",params));
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
            List<String> messageParams=new ArrayList<>();
            messageParams.add(ChatClientCache.getCurrentUserUsername());
            messageParams.add(ChatClientCache.getCurrentChannel());
            messageParams.add(messageText);
            String result=(String)XmlRpcAPI.getXmlRpcServer().execute("UserService.postMessage",messageParams);
            messageArea.setText("");
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

    }
}
