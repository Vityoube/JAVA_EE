package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.ApplicationStatuses;
import org.vkalashnykov.configuration.ChatClientCache;
import org.vkalashnykov.configuration.ErrorCodes;
import org.vkalashnykov.configuration.XmlRpcAPI;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by vkalashnykov on 02.01.17.
 */
public class BanUserController implements Initializable{

    public Label userToBan;
    public TextField banTime;
    public TextArea banCause;
    public Label timeError;
    public Label causeError;
    public Button cancelButton;
    public Label serverError;
    private boolean errorStatus=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userToBan.setText(ChatClientCache.getUsername());
        timeError.setVisible(false);
        serverError.setVisible(false);
        causeError.setVisible(false);
    }


    public void onCancel(ActionEvent event) {
        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


    public void onBanUser(ActionEvent event) {
        String banTimeInput=banTime.getText();
        String banCauseInput=banCause.getText();
        String currentUser=userToBan.getText();
        if ("".equals(banTimeInput)) {
            timeError(ErrorCodes.EMPTY_VALUE.getErrorDescription());
            setErrorStatus(true);
        }
        if ("".equals(banCauseInput)){
            causeError(ErrorCodes.EMPTY_VALUE.getErrorDescription());
            setErrorStatus(true);
        }
        if (isErrorStatus())
            return;
        List<String> banParams=new ArrayList<>();
        banParams.add(currentUser);
        banParams.add(banTimeInput);
        banParams.add(banCauseInput);
        try {
            XmlRpcAPI.getXmlRpcServer().execute("UserService.banUser",banParams);
        } catch (XmlRpcException e) {
            e.printStackTrace();
            serverError(e.getLocalizedMessage());
            return;
        }
        setErrorStatus(false);
        serverSuccess();
    }

    private void serverSuccess() {
        serverError.setVisible(true);
        serverError.setText(ApplicationStatuses.USER_BAN_SUCCESS.getStatusDescription());
        serverError.setTextFill(Color.GREEN);
    }

    private void setErrorStatus(boolean errorStatus) {
        this.errorStatus=errorStatus;
    }

    public boolean isErrorStatus(){
        return errorStatus;
    }

    public void timeError(String error){
        timeError.setText(error);
        timeError.setVisible(true);
        timeError.setTextFill(Color.RED);
        setErrorStatus(true);
    }

    public void causeError(String error){
        causeError.setText(error);
        causeError.setVisible(true);
        causeError.setTextFill(Color.RED);
    }

    public void serverError(String error){
        timeError.setVisible(false);
        causeError.setVisible(false);
        serverError.setText(error);
        serverError.setVisible(true);
        serverError.setTextFill(Color.RED);
    }
}
