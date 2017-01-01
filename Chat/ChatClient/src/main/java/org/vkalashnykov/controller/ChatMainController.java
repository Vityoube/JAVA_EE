package org.vkalashnykov.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startCurrentUsername.setText(ChatClientCache.getCurrentUserUsername());
        channelsList.setItems(FXCollections.observableList(ChatClientCache.getChannels()));
        channelsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                channel.setText(newValue);
                ChatClientCache.setCurrentChannel(newValue);
                List<String> params =new ArrayList<String>();
                params.add(ChatClientCache.getCurrentUserUsername());
                params.add(ChatClientCache.getCurrentChannel());
                try {
                    List<Object> usersOnChannel = Arrays.asList((Object[]) XmlRpcAPI.getXmlRpcServer().execute("UserService.enterChannel",params));
                    List<String> usersOnChannelList =new ArrayList<String>();
                    for(Object userOnChannel : usersOnChannel)
                        usersOnChannelList.add((String)userOnChannel);
                    ChatClientCache.setUsersOnChannel(usersOnChannelList);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        });
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
}
