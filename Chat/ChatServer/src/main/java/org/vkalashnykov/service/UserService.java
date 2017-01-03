package org.vkalashnykov.service;

import org.apache.xmlrpc.XmlRpcException;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public interface UserService extends UserDetailsService{
    public String closeUser(String username);
    public String banUser(String username, String banTime, String cause) throws XmlRpcException;
    public String createUser(@NotNull String username, @NotNull String password, String name, String lastName, String birthdate) throws XmlRpcException;
    public String login(@NotNull String username, @NotNull String password) throws XmlRpcException;
    public String logout(@NotNull String username);
    public Map<String, String> profile(@NotNull String username);
    public String modifyUserDetails(@NotNull String username, String firstName, String lastName, String birthdate) throws XmlRpcException;
    public String changePassword(@NotNull String username,
                                 @NotNull String currentPassword,
                                 @NotNull String newPassword,
                                 @NotNull String newPasswordConfirm) throws XmlRpcException;
    public List<String> channelsByStatus(@NotNull  String userStatus);
    public Map<String,String> enterChannel(@NotNull String username, @NotNull String channelName);
    public Map<String, String> channelDetails(@NotNull String channelName);
    public String exitChannel(@NotNull String username);
    public List<String> usersOnChannel(@NotNull String channelName);
    public String checkUserServerStatus(@NotNull String username);
    public String changeStatus(@NotNull String username, String status, String statusChangeCause);
    public String getUserStatus(String username);
    public String postMessage(String username, String channel, String messageText);


}
