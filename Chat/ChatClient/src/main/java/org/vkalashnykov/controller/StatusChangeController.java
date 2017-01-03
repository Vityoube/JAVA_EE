package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.api.ChatClientApi;
import org.vkalashnykov.api.XmlRpcAPI;
import org.vkalashnykov.configuration.*;

import java.net.URL;
import java.util.*;

/**
 * Created by vkalashnykov on 02.01.17.
 */
public class StatusChangeController implements Initializable{


    public SplitMenuButton statusSplitMenu;
    public TextArea causeOfChangeTextArea;
    public Label usernameLabel;
    public Label errorLabel;
    private List<String> userStatuses=new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameLabel.setText(ChatClientCache.getUsername());
        errorLabel.setVisible(false);
        for (UserStatuses userStatus : UserStatuses.values()){
            if (!userStatus.getRolename().equals(ChatClientCache.getProfileUserStatus()) && !userStatus.equals(UserStatuses.ADMIN))
                statusSplitMenu.getItems().add(new MenuItem(userStatus.getRolename()));

        }
        for (MenuItem userStatusItem : statusSplitMenu.getItems()){
            userStatusItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    statusSplitMenu.setText(userStatusItem.getText());
                }
            });
        }


    }

    public void onCancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onChangeStatus(ActionEvent event) {
        String status=statusSplitMenu.getText();
        String statusChangeCause =causeOfChangeTextArea.getText();
        try {

            String result= ChatClientApi.changeStatus(status,statusChangeCause);
            if (ServerStatuses.SUCCESS.name().equals(result)){
                ChatClientCache.setProfileUserStatus(status);
                success(ApplicationStatuses.USER_CHANGE_STATUS_SUCCESS.getStatusDescription());
            }
        } catch (XmlRpcException e) {
            e.printStackTrace();
            serverError(e.getLocalizedMessage());
        }

    }

    public void serverError(String error){
        errorLabel.setVisible(true);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText(error);
    }

    public void success(String message){
        errorLabel.setVisible(true);
        errorLabel.setTextFill(Color.GREEN);
        errorLabel.setText(message);
    }
}
