package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.ApplicationStatuses;
import org.vkalashnykov.configuration.ErrorCodes;
import org.vkalashnykov.configuration.ServerStatuses;
import org.vkalashnykov.configuration.XmlRpcConfiguration;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by vkalashnykov on 28.12.16.
 */
public class RegisterPageController implements Initializable {

    public Label registerStatus;
    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private PasswordField passwordConfirmInput;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField birthDateIput;

    @FXML
    private Label dateError;

    @FXML
    private Label usernameError;

    @FXML
    private Label passwordError;

    @FXML
    public Label passwordConfirmError;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void register(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        String passwordConfirm = passwordConfirmInput.getText();
        if (username.length() >= 6 && username.length() <= 20
                && password.length() >= 8 && password.length() <= 20
                && passwordConfirm.equals(password)) {
            String name = nameInput.getText();
            String lastName = lastNameInput.getText();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {

                List<Object> registrationParams = new ArrayList<Object>();
                registrationParams.add(username);
                registrationParams.add(password);
                registrationParams.add(name);
                registrationParams.add(lastName);
                Date birthdate=null;
                if (!"".equals(birthDateIput.getText())) {
                    birthdate = format.parse(birthDateIput.getText());

                } else {
                    birthdate=new Date();
                }
                registrationParams.add(birthdate);
                String registrationResult=(String) XmlRpcConfiguration.getXmlRpcServer().execute("UserService.createUser",registrationParams);
                if (ServerStatuses.SUCCESS.name().equals(registrationResult)){
                    usernameError.setText("");
                    dateError.setText("");
                    passwordError.setText("");
                    passwordConfirmError.setText("");
                    registerStatus.setTextFill(Color.GREEN);
                    registerStatus.setText(ApplicationStatuses.REGISTER_SUCCESS.getStatusDescription());
                }

            } catch (ParseException e) {
                e.printStackTrace();
                registerStatus.setTextFill(Color.RED);
                registerStatus.setText(ApplicationStatuses.REGISTER_ERROR.getStatusDescription());
                dateError.setText(ErrorCodes.WRONG_DATE_FORMAT.getErrorDescription());
            } catch (XmlRpcException e) {
                e.printStackTrace();
                registerStatus.setTextFill(Color.RED);
                registerStatus.setText(ApplicationStatuses.REGISTER_ERROR.getStatusDescription()+ ". Error: "+e.code+" "+e.getMessage());
            }
        } else {
            registerStatus.setTextFill(Color.RED);
            registerStatus.setText(ApplicationStatuses.REGISTER_ERROR.getStatusDescription());
            if (username.length() < 6)
                usernameError.setText(ErrorCodes.USERNAME_TOO_SHORT.getErrorDescription());
            if (username.length() > 20)
                usernameError.setText(ErrorCodes.USERNAME_TOO_LONG.getErrorDescription());
            if (password.length() < 8)
                passwordError.setText(ErrorCodes.PASSWORD_TOO_SHORT.getErrorDescription());
            if (password.length() > 20)
                passwordError.setText(ErrorCodes.PASSWORD_TOO_LONG.getErrorDescription());
            if (!passwordConfirm.equals(password))
                passwordConfirmError.setText(ErrorCodes.PASSWORDS_DO_NOT_MATCH.getErrorDescription());
        }
    }

    public void cleanUsernameError(KeyEvent keyEvent) {
        usernameError.setText("");
        registerStatus.setText("");
    }

    public void cleanPasswordError(KeyEvent keyEvent) {
        passwordError.setText("");
        registerStatus.setText("");
    }

    public void cleanPasswordConfirmError(KeyEvent keyEvent) {
        passwordConfirmError.setText("");
        registerStatus.setText("");
    }

    public void cleanBirthDateError(KeyEvent keyEvent) {
        dateError.setText("");
        registerStatus.setText("");
    }
}
