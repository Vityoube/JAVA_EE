package org.vkalashnykov.service;

import org.apache.xmlrpc.XmlRpcException;

import java.util.List;
import java.util.Map;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public interface UserService {
    public String closeUser(String username);
    public String banUser(String username, String banTime, String cause) throws XmlRpcException;
    public String createUser( String username,  String password, String name, String lastName, String birthdate) throws XmlRpcException;
    public String login( String username,  String password) throws XmlRpcException;
    public String logout( String username);
    public Map<String, String> profile( String username);
    public String modifyUserDetails( String username, String firstName, String lastName, String birthdate) throws XmlRpcException;
    public String changePassword( String username,
                                  String currentPassword,
                                  String newPassword,
                                  String newPasswordConfirm) throws XmlRpcException;
    public List<Object> channelsByStatus( String userStatus);
    public Map<String,String> enterChannel( String username,  String channelName);
    public Map<String, String> channelDetails( String channelName);
    public String exitChannel( String username);
    public List<Object> usersOnChannel( String channelName);
    public String checkUserServerStatus( String username);
    public String changeStatus( String username, String status, String statusChangeCause);
    public String getUserStatus(String username);
    public String postMessage(String username, String channel, String messageText);
    public List<Object> messagesOnChannel(String channel,String username);

}
