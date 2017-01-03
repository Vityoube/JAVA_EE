package org.vkalashnykov.api;

import org.apache.xmlrpc.XmlRpcException;
import org.vkalashnykov.configuration.ChatClientCache;
import org.vkalashnykov.configuration.FrameworkConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public class ChatClientApi {

    public static String login(String login,String password) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> loginCredentials=new ArrayList<>();
            loginCredentials.add(login);
            loginCredentials.add(password);
            return (String)XmlRpcAPI.execute("UserService.login", loginCredentials);
        }
        return null;
    }

    public static Map<String, String> profile(String username) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> profileUsername=new ArrayList<>();
            profileUsername.add(username);
            return (Map<String,String>)XmlRpcAPI.execute("UserService.profile",profileUsername);
        }
        return null;

    }

    public static List<Object> channelsByStatus() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> currentStatus=new ArrayList<>();
            currentStatus.add(ChatClientCache.getCurrentUserStatus());
            return Arrays.asList( (Object[]) XmlRpcAPI.execute("UserService.channelsByStatus",currentStatus));
        }
        return null;
    }




    public static boolean isXMLRPC(){
        if(FrameworkConfiguration.Frameworks.XMLRPC.getImplementation().equals(FrameworkConfiguration.getFrameworkImplementation()))
            return true;
        return  false;
    }

    public static boolean isHessian(){
        if(FrameworkConfiguration.Frameworks.HESSIAN.getImplementation().equals(FrameworkConfiguration.getFrameworkImplementation()))
            return true;
        return  false;
    }

    public static boolean isBurlap(){
        if(FrameworkConfiguration.Frameworks.BURLAP.getImplementation().equals(FrameworkConfiguration.getFrameworkImplementation()))
            return true;
        return  false;
    }

    public static List<Object> messagesOnChannel(String currentChannel, String currentUserUsername) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> channelParams=new ArrayList<>();
            channelParams.add(currentChannel);
            channelParams.add(currentUserUsername);
            return Arrays.asList((Object[]) XmlRpcAPI.execute("UserService.messagesOnChannel",channelParams));
        }
        return null;
    }

    public static String getUserStatus() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            String currentUserStatus=(String)XmlRpcAPI.execute("UserService.getUserStatus",username);
            return currentUserStatus;
        }
        return null;
    }

    public static String logout() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            return (String) XmlRpcAPI.execute("UserService.logout",username);
        }
        return null;
    }

    public static Map<String,String> channelDetails() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> channel = new ArrayList<>();
            channel.add(ChatClientCache.getCurrentChannel());
            Map<String,String> channelDetails=(Map) XmlRpcAPI.execute("UserService.channelDetails",channel);
            return  channelDetails;
        }
        return null;
    }

    public static String checkUserServerStatus() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            return (String)XmlRpcAPI.execute("UserService.checkUserServerStatus",username);
        }
        return  null;
    }

    public static Map<String,String> enterChannel() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> channelParams =new ArrayList<String>();
            channelParams.add(ChatClientCache.getCurrentUserUsername());
            channelParams.add(ChatClientCache.getCurrentChannel());
            return (Map<String,String>) XmlRpcAPI.execute("UserService.enterChannel",channelParams);
        }
        return null;
    }

    public static List<Object> usersOnChannel() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> channelName=new ArrayList<>();
            channelName.add(ChatClientCache.getCurrentChannel());
            return Arrays.asList((Object[])XmlRpcAPI.execute("UserService.usersOnChannel",channelName));
        }
        return null;
    }

    public static String sendMessage(String messageText) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> messageParams=new ArrayList<>();
            messageParams.add(ChatClientCache.getCurrentUserUsername());
            messageParams.add(ChatClientCache.getCurrentChannel());
            messageParams.add(messageText);
            String result=(String)XmlRpcAPI.execute("UserService.postMessage",messageParams);
        }

        return null;
    }

    public static String modifyUserDetails(String firstName, String lastName, String birthDate) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> modifiedParams = new ArrayList<>();
            modifiedParams.add(ChatClientCache.getCurrentUserUsername());
            modifiedParams.add(firstName);
            modifiedParams.add(lastName);
            modifiedParams.add(birthDate);
            return (String) XmlRpcAPI.execute("UserService.modifyUserDetails",modifiedParams);
        }
        return null;

    }

    public static String closeUser() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getUsername());
            return (String) XmlRpcAPI.execute("UserService.closeUser",username);
        }
        return null;
    }

    public static String changePassword(String currentpassword,String newPassword,String passwordConfirm) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> changePasswordParams = new ArrayList<>();
            changePasswordParams.add(ChatClientCache.getCurrentUserUsername());
            changePasswordParams.add(currentpassword);
            changePasswordParams.add(newPassword);
            changePasswordParams.add(passwordConfirm);
            return (String) XmlRpcAPI.execute("UserService.changePassword",changePasswordParams);
        }
        return  null;
    }


    public static String banUser(String currentUser, String banTimeInput, String banCauseInput) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> banParams=new ArrayList<>();
            banParams.add(currentUser);
            banParams.add(banTimeInput);
            banParams.add(banCauseInput);
            return (String)XmlRpcAPI.execute("UserService.banUser",banParams);
        }
        return null;

    }

    public static String changeStatus(String status, String statusChangeCause) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> changeParams=new ArrayList<>();
            changeParams.add(ChatClientCache.getUsername());
            changeParams.add(status);
            changeParams.add(statusChangeCause);
            return (String) XmlRpcAPI.execute("UserService.changeStatus",changeParams);
        }
        return null;
    }

    public static String register(String username, String password, String name, String lastName, String birthdate) throws XmlRpcException {
        if (isXMLRPC()){
            List<Object> registrationParams = new ArrayList<Object>();
            registrationParams.add(username);
            registrationParams.add(password);
            registrationParams.add(name);
            registrationParams.add(lastName);
            registrationParams.add(birthdate);
            return  (String) XmlRpcAPI.getXmlRpcServer().execute("UserService.createUser",registrationParams);
        }
        return null;
    }
}
