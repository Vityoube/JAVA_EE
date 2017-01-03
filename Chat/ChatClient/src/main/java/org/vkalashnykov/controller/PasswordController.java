package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.api.ChatClientApi;
import org.vkalashnykov.configuration.ChatClientCache;
import org.vkalashnykov.configuration.ErrorCodes;
import org.vkalashnykov.configuration.ServerStatuses;
import org.vkalashnykov.api.XmlRpcAPI;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public class PasswordController implements Initializable {

    public Label currentPasswordError;
    public Label newPasswordError;
    public Label passwordConfirmError;
    public PasswordField currentPassword;
    public PasswordField newPassword;
    public PasswordField passwordConfirm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void onCancel(ActionEvent event) {
        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();

    }

    public void onChange(ActionEvent event) throws XmlRpcException {
        String currentPasswordInput =currentPassword.getText();
        String newPasswordInput=newPassword.getText();
        String passwordConfirmInput =passwordConfirm.getText();
        currentPasswordError.setVisible(false);
        newPasswordError.setVisible(false);
        passwordConfirmError.setVisible(false);
        if (!currentPasswordInput.isEmpty() && !newPasswordInput.isEmpty() && !passwordConfirmInput.isEmpty()){
            String result= ChatClientApi.changePassword(currentPasswordInput,newPasswordInput,passwordConfirmInput);
            if (ServerStatuses.SUCCESS.name().equals(result)){
                Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                if (ErrorCodes.WRONG_PASSWORD.getErrorDescription().equals(result)){
                    currentPasswordError.setVisible(true);
                    currentPasswordError.setText(result);
                }

                if (ErrorCodes.PASSWORD_TOO_SHORT.getErrorDescription().equals(result)){
                    newPasswordError.setVisible(true);
                    newPasswordError.setText(result);
                }

                if (ErrorCodes.PASSWORD_TOO_LONG.getErrorDescription().equals(result)){
                    newPasswordError.setVisible(true);
                    newPasswordError.setText(result);
                }

                if (ErrorCodes.PASSWORDS_DO_NOT_MATCH.getErrorDescription().equals(result)){
                    passwordConfirmError.setVisible(true);
                    passwordConfirmError.setText(result);
                }

            }
        } else {
            if (currentPasswordInput.isEmpty())
                currentPasswordError.setText(ErrorCodes.PASSWORD_TOO_SHORT.getErrorDescription());{
                currentPasswordError.setVisible(true);
            }
            if (newPasswordInput.isEmpty()){
                newPasswordError.setVisible(true);
                newPasswordError.setText(ErrorCodes.PASSWORD_TOO_SHORT.getErrorDescription());
            }

            if (passwordConfirmInput.isEmpty()){
                passwordConfirmError.setVisible(true);
                passwordConfirmError.setText(ErrorCodes.PASSWORD_TOO_SHORT.getErrorDescription());
            }

        }

    }
}
