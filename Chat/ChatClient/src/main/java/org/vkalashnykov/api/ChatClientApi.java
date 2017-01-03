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
            return (String)XmlRpcAPI.execute("UserServiceImpl.login", loginCredentials);
        }
         if (isHessian()){
            return HessianAPI.getUserService().login(login,password);
         }
         if (isBurlap())
             return BurlapAPI.getUserService().login(login,password);
        return null;
    }

    public static Map<String, String> profile(String username) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> profileUsername=new ArrayList<>();
            profileUsername.add(username);
            return (Map<String,String>)XmlRpcAPI.execute("UserServiceImpl.profile",profileUsername);
        }
        if (isHessian())
            return HessianAPI.getUserService().profile(username);
        if (isBurlap())
            return BurlapAPI.getUserService().profile(username);
        return null;

    }

    public static List<Object> channelsByStatus() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> currentStatus=new ArrayList<>();
            currentStatus.add(ChatClientCache.getCurrentUserStatus());
            return Arrays.asList( (Object[]) XmlRpcAPI.execute("UserServiceImpl.channelsByStatus",currentStatus));
        }
        if (isHessian())
            return HessianAPI.getUserService().channelsByStatus(ChatClientCache.getCurrentUserStatus());
        if (isBurlap())
            return BurlapAPI.getUserService().channelsByStatus(ChatClientCache.getCurrentUserStatus());
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
            return Arrays.asList((Object[]) XmlRpcAPI.execute("UserServiceImpl.messagesOnChannel",channelParams));
        }
        if (isHessian())
            return HessianAPI.getUserService().messagesOnChannel(currentChannel,currentUserUsername);
        if (isBurlap())
            return BurlapAPI.getUserService().messagesOnChannel(currentChannel,currentUserUsername);
        return null;
    }

    public static String getUserStatus() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            String currentUserStatus=(String)XmlRpcAPI.execute("UserServiceImpl.getUserStatus",username);
            return currentUserStatus;
        }
        if (isHessian())
            return HessianAPI.getUserService().getUserStatus(ChatClientCache.getCurrentUserUsername());
        if (isBurlap())
            return BurlapAPI.getUserService().getUserStatus(ChatClientCache.getCurrentUserUsername());
        return null;
    }

    public static String logout() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            return (String) XmlRpcAPI.execute("UserServiceImpl.logout",username);
        }
        if (isHessian())
            return HessianAPI.getUserService().logout(ChatClientCache.getCurrentUserUsername());
        if (isBurlap())
            return BurlapAPI.getUserService().logout(ChatClientCache.getCurrentUserUsername());
        return null;
    }

    public static Map<String,String> channelDetails() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> channel = new ArrayList<>();
            channel.add(ChatClientCache.getCurrentChannel());
            Map<String,String> channelDetails=(Map) XmlRpcAPI.execute("UserServiceImpl.channelDetails",channel);
            return  channelDetails;
        }
        if (isHessian())
            return HessianAPI.getUserService().channelDetails(ChatClientCache.getCurrentChannel());
        if (isBurlap())
            return BurlapAPI.getUserService().channelDetails(ChatClientCache.getCurrentChannel());
        return null;
    }

    public static String checkUserServerStatus() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getCurrentUserUsername());
            return (String)XmlRpcAPI.execute("UserServiceImpl.checkUserServerStatus",username);
        }
        if (isHessian())
            return HessianAPI.getUserService().checkUserServerStatus(ChatClientCache.getCurrentUserUsername());
        if (isBurlap())
            return BurlapAPI.getUserService().checkUserServerStatus(ChatClientCache.getCurrentUserUsername());
        return  null;
    }

    public static Map<String,String> enterChannel() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> channelParams =new ArrayList<String>();
            channelParams.add(ChatClientCache.getCurrentUserUsername());
            channelParams.add(ChatClientCache.getCurrentChannel());
            return (Map<String,String>) XmlRpcAPI.execute("UserServiceImpl.enterChannel",channelParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().enterChannel(ChatClientCache.getCurrentUserUsername(),ChatClientCache.getCurrentChannel());
        if (isBurlap())
            return BurlapAPI.getUserService().enterChannel(ChatClientCache.getCurrentUserUsername(),ChatClientCache.getCurrentChannel());
        return null;
    }

    public static List<Object> usersOnChannel() throws XmlRpcException {
        if (isXMLRPC()){
            List<String> channelName=new ArrayList<>();
            channelName.add(ChatClientCache.getCurrentChannel());
            return Arrays.asList((Object[])XmlRpcAPI.execute("UserServiceImpl.usersOnChannel",channelName));
        }
        if (isHessian())
            return HessianAPI.getUserService().usersOnChannel(ChatClientCache.getCurrentChannel());
        if (isBurlap())
            return BurlapAPI.getUserService().usersOnChannel(ChatClientCache.getCurrentChannel());
        return null;
    }

    public static String sendMessage(String messageText) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> messageParams=new ArrayList<>();
            messageParams.add(ChatClientCache.getCurrentUserUsername());
            messageParams.add(ChatClientCache.getCurrentChannel());
            messageParams.add(messageText);
            String result=(String)XmlRpcAPI.execute("UserServiceImpl.postMessage",messageParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().postMessage(ChatClientCache.getCurrentUserUsername(),
                    ChatClientCache.getCurrentChannel(),messageText);
        if (isBurlap())
            return BurlapAPI.getUserService().postMessage(ChatClientCache.getCurrentUserUsername(),
                    ChatClientCache.getCurrentChannel(),messageText);

        return null;
    }

    public static String modifyUserDetails(String firstName, String lastName, String birthDate) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> modifiedParams = new ArrayList<>();
            modifiedParams.add(ChatClientCache.getCurrentUserUsername());
            modifiedParams.add(firstName);
            modifiedParams.add(lastName);
            modifiedParams.add(birthDate);
            return (String) XmlRpcAPI.execute("UserServiceImpl.modifyUserDetails",modifiedParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().modifyUserDetails(ChatClientCache.getCurrentUserUsername(),firstName,lastName,birthDate);
        if (isBurlap())
            return BurlapAPI.getUserService().modifyUserDetails(ChatClientCache.getCurrentUserUsername(),firstName,lastName,birthDate);
        return null;

    }

    public static String closeUser() throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> username=new ArrayList<>();
            username.add(ChatClientCache.getUsername());
            return (String) XmlRpcAPI.execute("UserServiceImpl.closeUser",username);
        }
        if (isHessian())
            return HessianAPI.getUserService().closeUser(ChatClientCache.getUsername());
        if (isBurlap())
            return BurlapAPI.getUserService().closeUser(ChatClientCache.getUsername());
        return null;
    }

    public static String changePassword(String currentpassword,String newPassword,String passwordConfirm) throws XmlRpcException {
        if (isXMLRPC()){
            List<String> changePasswordParams = new ArrayList<>();
            changePasswordParams.add(ChatClientCache.getCurrentUserUsername());
            changePasswordParams.add(currentpassword);
            changePasswordParams.add(newPassword);
            changePasswordParams.add(passwordConfirm);
            return (String) XmlRpcAPI.execute("UserServiceImpl.changePassword",changePasswordParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().changePassword(ChatClientCache.getCurrentUserUsername(),currentpassword,
                    newPassword,passwordConfirm);
        if (isBurlap())
            return BurlapAPI.getUserService().changePassword(ChatClientCache.getCurrentUserUsername(),currentpassword,
                    newPassword,passwordConfirm);
        return  null;
    }


    public static String banUser(String currentUser, String banTimeInput, String banCauseInput) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> banParams=new ArrayList<>();
            banParams.add(currentUser);
            banParams.add(banTimeInput);
            banParams.add(banCauseInput);
            return (String)XmlRpcAPI.execute("UserServiceImpl.banUser",banParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().banUser(currentUser,banTimeInput,banCauseInput);
        if (isBurlap())
            return BurlapAPI.getUserService().banUser(currentUser,banTimeInput,banCauseInput);
        return null;

    }

    public static String changeStatus(String status, String statusChangeCause) throws XmlRpcException {
        if (isXMLRPC()) {
            List<String> changeParams=new ArrayList<>();
            changeParams.add(ChatClientCache.getUsername());
            changeParams.add(status);
            changeParams.add(statusChangeCause);
            return (String) XmlRpcAPI.execute("UserServiceImpl.changeStatus",changeParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().changeStatus(ChatClientCache.getUsername(),status,statusChangeCause);
        if (isBurlap())
            return BurlapAPI.getUserService().changeStatus(ChatClientCache.getUsername(),status,statusChangeCause);
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
            return  (String) XmlRpcAPI.getXmlRpcServer().execute("UserServiceImpl.createUser",registrationParams);
        }
        if (isHessian())
            return HessianAPI.getUserService().createUser(username,password,name,lastName,birthdate);
        if (isBurlap())
            return BurlapAPI.getUserService().createUser(username,password,name,lastName,birthdate);
        return null;
    }
}
