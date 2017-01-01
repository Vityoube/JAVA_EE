package org.vkalashnykov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public class ProfileController implements Initializable{

    public Label profileUsername;
    public Label profileLastLoginDate;
    public Label profileRegistrationStatus;
    public Label profileUserStatus;
    public Label profileOnlineStatus;
    public TextField profileFirstName;
    public TextField profileLastName;
    public DatePicker profileBirthDate;
    public Button editButton;
    public Button cancelButton;
    public Label status;
    public Button closeButton;
    public Label lastLoginLabel;
    public Hyperlink passwordChange;
    public Hyperlink changeStatus;
    public Button banButton;
    public Label lastActionLabel;
    public Label profileLastActionDate;
    private boolean editMode=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showPublicDetails();
        showCurrentUserDetails();
        showNotCurrentProfile();
    }


    public void onEdit(ActionEvent event) {
        if (!editMode){
            setEditMode(true);
            editButton.setText("Save");
            cancelButton.setVisible(true);
            cancelButton.setDisable(false);
            profileFirstName.setEditable(true);
            profileLastName.setEditable(true);
            profileBirthDate.setEditable(true);
        } else{
            String modifiedFirstName=profileFirstName.getText();
            String modifiedLastName=profileLastName.getText();
            String modifiedBirthDate=profileBirthDate.getEditor().getText();
            List<String> params = new ArrayList<>();
            params.add(ChatClientCache.getCurrentUserUsername());
            params.add(modifiedFirstName);
            params.add(modifiedLastName);
            params.add(modifiedBirthDate);
            try {
                String result=(String) XmlRpcAPI.getXmlRpcServer().execute("UserService.modifyUserDetails",params);
                if (ServerStatuses.SUCCESS.name().equals(result)){
                    editButton.setText("Edit");
                    cancelButton.setVisible(false);
                    profileFirstName.setEditable(false);
                    profileLastName.setEditable(false);
                    profileBirthDate.setEditable(false);
                    setEditMode(false);
                    if(!modifiedBirthDate.equals(ChatClientCache.getCurrentUserProfile().get("birthdate")) ||
                    !modifiedFirstName.equals(ChatClientCache.getCurrentUserProfile().get("firstname"))
                            || !modifiedLastName.equals(ChatClientCache.getCurrentUserProfile().get("lastname")) ){
                        ChatClientCache.getCurrentUserProfile().replace("firstname",modifiedFirstName);
                        ChatClientCache.getCurrentUserProfile().replace("lastname",modifiedLastName);
                        ChatClientCache.getCurrentUserProfile().replace("birthdate",modifiedBirthDate);
                        status.setVisible(true);
                        status.setTextFill(Color.GREEN);
                        status.setText(ApplicationStatuses.CHANGE_SUCCESS.getStatusDescription());
                    }
                }
            } catch (XmlRpcException e) {
                e.printStackTrace();
                status.setVisible(true);
                status.setTextFill(Color.RED);
                status.setText(ApplicationStatuses.CHANGE_ERROR.getStatusDescription()+". Error: "+e.code+" "+e.getMessage());
            }
        }


    }

    public void onCancel(ActionEvent event) {
        setEditMode(false);
        cancelButton.setVisible(false);
        cancelButton.setDisable(true);
        editButton.setText("Edit");
        profileFirstName.setText(ChatClientCache.getUserProfile().get("firstname"));
        profileLastName.setText(ChatClientCache.getUserProfile().get("lastname"));
        profileBirthDate.getEditor().setText(ChatClientCache.getUserProfile().get("birthdate"));
        profileBirthDate.setEditable(false);
        profileFirstName.setEditable(false);
        profileLastName.setEditable(false);
        status.setVisible(false);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        setEditMode(false);
        cancelButton.setVisible(false);
        cancelButton.setDisable(true);
        editButton.setText("Edit");
        profileFirstName.setText(ChatClientCache.getUserProfile().get("firstname"));
        profileLastName.setText(ChatClientCache.getUserProfile().get("lastname"));
        profileBirthDate.getEditor().setText(ChatClientCache.getUserProfile().get("birthdate"));
        profileBirthDate.setEditable(false);
        profileFirstName.setEditable(false);
        profileLastName.setEditable(false);
        status.setVisible(false);
    }

    public void onChangePassword(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("/fxml/password_change.fxml"));
        Stage stage=new Stage();
        Scene scene=new Scene(root);
        stage.setTitle("Change password");
        stage.setScene(scene);
        stage.show();

    }

    public void onCloseProfile(ActionEvent event) throws XmlRpcException, IOException {
        Stage stage;
        Parent root;
        List<String> params=new ArrayList<>();
        params.add(ChatClientCache.getUsername());
        String result=(String) XmlRpcAPI.getXmlRpcServer().execute("UserService.closeUser",params);
        if (ServerStatuses.SUCCESS.name().equals(result)){
            ChatClientCache.cleanCache();
            root= FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene=new Scene(root);
            stage= (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void showPublicDetails(){
        profileUsername.setText(ChatClientCache.getUsername());
        profileUserStatus.setText(ChatClientCache.getProfileUserStatus());
        profileFirstName.setText(ChatClientCache.getUserProfile().get("firstname"));
        profileLastName.setText(ChatClientCache.getUserProfile().get("lastname"));
        profileBirthDate.getEditor().setText(ChatClientCache.getUserProfile().get("birthdate"));
        profileUserStatus.setText(ChatClientCache.getUserProfile().get("userStatus"));
    }

    public void showCurrentUserDetails(){
        passwordChange.setVisible(ChatClientCache.isCurrentUser()? true : false);
        passwordChange.setDisable(ChatClientCache.isCurrentUser() ? false :true);
        editButton.setVisible(ChatClientCache.isCurrentUser() ? true : false);
        editButton.setDisable(ChatClientCache.isCurrentUser() ? false : true);
        closeButton.setDisable(ChatClientCache.isCurrentUser() || ChatClientCache.isAdmin()? false : true);
        closeButton.setVisible(ChatClientCache.isCurrentUser() || ChatClientCache.isAdmin()? true : false);
        cancelButton.setDisable(true);
        cancelButton.setVisible(false);
    }

    private void showNotCurrentProfile() {
        if (!ChatClientCache.isCurrentUser()){
            profileRegistrationStatus.setDisable(false);
            profileRegistrationStatus.setVisible(true);
            profileRegistrationStatus.setText(ChatClientCache.getUserProfile().get("registrationStatus"));
            profileOnlineStatus.setVisible(true);
            profileOnlineStatus.setDisable(false);
            profileOnlineStatus.setText(ChatClientCache.getUserProfile().get("onlineStatus"));
            lastActionLabel.setVisible(true);
            profileLastActionDate.setVisible(true);
            if (ChatClientCache.getUserProfile().get("blockDate")!=null){
                profileLastActionDate.setText(ChatClientCache.getUserProfile().get("blockDate"));
                lastActionLabel.setText(LastActions.BLOCK.getActionName());
            }

            else if (ChatClientCache.getUserProfile().get("closeDate")!=null){
                profileLastActionDate.setText(ChatClientCache.getUserProfile().get("closeDate"));
                lastActionLabel.setText(LastActions.CLOSE.getActionName());
            }

            else if (ChatClientCache.getUserProfile().get("registrationDate")!=null){
                profileLastActionDate.setText(ChatClientCache.getUserProfile().get("registrationDate"));
                lastActionLabel.setText(LastActions.REGISTRATION.getActionName());
            } else {
                lastActionLabel.setVisible(false);
                profileLastActionDate.setVisible(false);
            }
            if (ChatClientCache.getUserProfile().get("lastLoginDate")!=null &&
                    profileOnlineStatus.getText().equals("Online")){
                lastLoginLabel.setVisible(true);
                profileLastLoginDate.setVisible(true);
                profileLastLoginDate.setText(ChatClientCache.getUserProfile().get("lastLoginDate"));
            }
            if (ChatClientCache.isAdmin() || ChatClientCache.isModerator()) {
                banButton.setVisible(true);
                banButton.setDisable(false);
                changeStatus.setDisable(false);
                changeStatus.setVisible(true);
            } else {
                banButton.setVisible(false);
                banButton.setDisable(true);
                changeStatus.setDisable(true);
                changeStatus.setVisible(false);
            }
        }
    }

}
