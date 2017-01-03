package org.vkalashnykov.service;

import com.google.common.collect.ImmutableList;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vkalashnykov.configuration.MessageStatuses;
import org.vkalashnykov.configuration.ServerErrors;
import org.vkalashnykov.configuration.Statuses;
import org.vkalashnykov.configuration.UserServerStatus;
import org.vkalashnykov.model.*;
import org.vkalashnykov.persistence.ChannelDAO;
import org.vkalashnykov.persistence.UserDAO;
import org.vkalashnykov.utils.DateTimeUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ChannelDAO channelDAO;

    private Map<String,List<Message>> messagesOnChannel =new TreeMap<>();

    private Channel currentChannel;

    private Date unblockTime;

    @PostConstruct
    public void init() {
        if (!userDAO.findByUsername("admin").isPresent()) {
            userDAO.save(User.builder()
                    .username("admin")
                    .password(new BCryptPasswordEncoder().encode("admin"))
                    .authorities(ImmutableList.of(UserRoles.USER, UserRoles.ADMIN))
                    .onlineStatus(OnlineStatuses.OFFLINE.getStatusDescription())
                    .registrationDate(DateTimeUtil.getApplicationStartDate())
                    .build());

        }
        if (!channelDAO.findByChannelName("user_channel").isPresent()){
            channelDAO.save(Channel.builder()
                    .channelName("user_channel")
                    .allowedStatus(ChannelStatuses.USER.getStatusName())
                    .description("Just ordinary channel for chating")
                    .build());
        }
        if (!channelDAO.findByChannelName("flame").isPresent()){
            channelDAO.save(Channel.builder()
                    .channelName("flame")
                    .allowedStatus(ChannelStatuses.USER.getStatusName())
                    .description("Old good flame channel")
                    .build());
        }
        if (!channelDAO.findByChannelName("vip_channel").isPresent()){
            channelDAO.save(Channel.builder()
                    .channelName("vip_channel")
                    .allowedStatus(ChannelStatuses.VIP.getStatusName())
                    .description("Channel for VIPs")
                    .build());
        }
        if (!channelDAO.findByChannelName("moderator_channel").isPresent()){
            channelDAO.save(Channel.builder()
                    .channelName("moderator_channel")
                    .allowedStatus(ChannelStatuses.MODERATOR.getStatusName())
                    .description("Only moderators can get there.")
                    .build());
        }

    }

    //    @Cacheable("messagesOnChannel")
//    public List<Message> getMessages(User user) {
//        return user.getMessages();
//    }
//
//    @CachePut(value = "messagesOnChannel")
//    public void sendMessage(String text, String username) {
//        User userTo = userDAO.findByUsername(username).orElse(null);
//        User currentUser = userDAO.findByUsername(currentUserUsername).orElse(null);
//        Message message = new Message(currentUser, userTo, text);
//        userTo.addMessage(message);
//        userTo.addMessage(message);
//    }

    public String closeUser(String username) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        user.setRegistrationStatus(RegistrationStatuses.CLOSED.name());
        user.setCloseDate(DateTimeUtil.getCurrentDate());
        userDAO.save(user);
        logout(username);
        return Statuses.SUCCESS.name();
    }

    public String banUser(String username, String banTime, String cause) throws XmlRpcException {
        long banTimeLong=0;
        try{
            banTimeLong=Long.parseLong(banTime);
        } catch(Exception e) {
            throw new XmlRpcException(ServerErrors.WRONG_NUMBER_FORMAT.getErrorDescrition());
        }
        if (banTimeLong<0)
            throw new XmlRpcException(ServerErrors.WRONG_NUMBER_FORMAT.getErrorDescrition());
        logout(username);
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        user.setRegistrationStatus(RegistrationStatuses.BANNED.name());
        user.setBlockDate(DateTimeUtil.getCurrentDate());
        user.setBlockCause(cause);
        user.setUnblockDate(DateTimeUtil.plus(banTimeLong*1000*60));
        userDAO.save(user);

        return Statuses.SUCCESS.name();
    }

    public String createUser(@NotNull String username, @NotNull String password, String name, String lastName, String birthdate) throws XmlRpcException {
        try {
            if (DateTimeUtil.convertStringToDate(birthdate).after(DateTimeUtil.getCurrentDate()))
                throw new XmlRpcException(403, "The birth date should be less than current date");
            else if (!DateTimeUtil.isEmptyStringDate(birthdate)) {
                if (!userDAO.findByUsername(username).isPresent()) {
                    userDAO.save(User.builder()
                            .username(username)
                            .password(new BCryptPasswordEncoder().encode(password))
                            .registrationStatus(RegistrationStatuses.PENDING.name())
                            .authorities(ImmutableList.of(UserRoles.USER))
                            .registrationDate(DateTimeUtil.getCurrentDate())
                            .onlineStatus(OnlineStatuses.OFFLINE.getStatusDescription())
                            .birthDate(DateTimeUtil.convertStringToDate(birthdate))
                            .firstName(name)
                            .lastName(lastName)
                            .build());
                    User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
                    user.setRegistrationStatus(RegistrationStatuses.REGISTERED.name());
                    userDAO.save(user);
                    return Statuses.SUCCESS.name();
                } else {
                    throw new XmlRpcException(ServerErrors.USER_EXISTS.getErrorDescrition());
                }
            } else {
                if (!userDAO.findByUsername(username).isPresent()) {
                    userDAO.save(User.builder()
                            .username(username)
                            .password(new BCryptPasswordEncoder().encode(password))
                            .registrationStatus(RegistrationStatuses.PENDING.name())
                            .authorities(ImmutableList.of(UserRoles.USER))
                            .registrationDate(DateTimeUtil.getCurrentDate())
                            .onlineStatus(OnlineStatuses.OFFLINE.getStatusDescription())
                            .firstName(name)
                            .lastName(lastName)
                            .build());
                    User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
                    user.setRegistrationStatus(RegistrationStatuses.REGISTERED.name());
                    userDAO.save(user);
                    return Statuses.SUCCESS.name();
                } else {
                    throw new XmlRpcException(422, ServerErrors.USER_EXISTS.getErrorDescrition());
                }
            }
        } catch (ParseException e) {
            throw new XmlRpcException(422, "The Date should be in dd.mm.yyy format");
        }
    }

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        return userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));  // Here the userDAO is null
    }


    public String login(@NotNull String username, @NotNull String password) throws XmlRpcException {
        try {
            User user = (User) loadUserByUsername(username);
            if (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                    if (OnlineStatuses.ONLINE.getStatusDescription().equals(user.getOnlineStatus()))
                    throw new XmlRpcException(403, ServerErrors.USER_ONLINE.getErrorDescrition());
                if (!RegistrationStatuses.CLOSED.name().equals(user.getRegistrationStatus()) &&
                        !RegistrationStatuses.BANNED.name().equals(user.getRegistrationStatus())){
                    user.setOnlineStatus(OnlineStatuses.ONLINE.getStatusDescription());
                } else if (RegistrationStatuses.CLOSED.name().equals(user.getRegistrationStatus())){
                    throw new UsernameNotFoundException("user " + username + " was not found");
                } else if (RegistrationStatuses.BANNED.name().equals(user.getRegistrationStatus())  && DateTimeUtil.getCurrentDate().before(user.getUnblockDate())){
                    return ServerErrors.USER_BANNED.getErrorDescrition();
                } else if(RegistrationStatuses.BANNED.name().equals(user.getRegistrationStatus()) && DateTimeUtil.getCurrentDate().after(user.getUnblockDate())){
                    user.setRegistrationStatus(RegistrationStatuses.REGISTERED.name());
                    user.setBlockDate(null);
                    user.setBlockCause(null);
                }
                user.setLoginTime(DateTimeUtil.getCurrentDate());
                userDAO.save(user);
                return Statuses.SUCCESS.name();

            } else
                return ServerErrors.WRONG_CREDENTIALS.getErrorDescrition();
        } catch (UsernameNotFoundException e) {
            return ServerErrors.WRONG_CREDENTIALS.getErrorDescrition();
        }
    }

    public String logout(@NotNull String username) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        exitChannel(username);
        user.setOnlineStatus(OnlineStatuses.OFFLINE.getStatusDescription());
        user.setLastLoginDate(DateTimeUtil.getCurrentDate());
        userDAO.save(user);
        return Statuses.SUCCESS.name();
    }

    public Map<String, String> profile(@NotNull String username) {
        Map<String, String> userProfile = new HashMap<>();
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        userProfile.put("username", user.getUsername());
        userProfile.put("onlineStatus", user.getOnlineStatus());
        userProfile.put("registrationStatus", user.getRegistrationStatus());
        userProfile.put("registrationDate", DateTimeUtil.convertDatetoString(user.getRegistrationDate()));
        userProfile.put("closeDate", DateTimeUtil.convertDatetoString(user.getCloseDate()));
        userProfile.put("blockDate", DateTimeUtil.convertDatetoString(user.getBlockDate()));
        userProfile.put("userStatus", user.getAuthorities().get(user.getAuthorities().size() - 1).getAuthority());
        userProfile.put("lastLoginDate", DateTimeUtil.convertDatetoString(user.getLastLoginDate()));
        userProfile.put("firstname", user.getFirstName());
        userProfile.put("lastname", user.getLastName());
        userProfile.put("birthdate", DateTimeUtil.convertDatetoString(user.getBirthDate()));
        return userProfile;
    }

    public String modifyUserDetails(@NotNull String username, String firstName, String lastName, String birthdate) throws XmlRpcException {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        try {
            if (DateTimeUtil.convertStringToDate(birthdate).after(DateTimeUtil.getCurrentDate()))
                throw new XmlRpcException(403, "The birth date should be less than current date");
            else if (!DateTimeUtil.isEmptyStringDate(birthdate)) {
                user.setBirthDate(DateTimeUtil.convertStringToDate(birthdate));
            }
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userDAO.save(user);
            return Statuses.SUCCESS.name();
        } catch (ParseException e) {
            throw new XmlRpcException(422, "The Date should be in dd.mm.yyy format");
        }
    }

    public String changePassword(@NotNull String username, @NotNull String currentPassword, @NotNull String newPassword, @NotNull String newPasswordConfirm) throws XmlRpcException {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        if (new BCryptPasswordEncoder().matches(currentPassword, user.getPassword())) {
            if (newPassword.length() >= 6 && newPassword.length() <= 20) {
                if (newPassword.equals(newPasswordConfirm)) {
                    user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                    userDAO.save(user);
                    return Statuses.SUCCESS.name();
                } else {
                    return ServerErrors.PASSWORDS_DO_NOT_MATCH.getErrorDescrition();
                }
            } else if (newPassword.length() < 6) {
                return ServerErrors.PASSWORD_TOO_SHORT.getErrorDescrition();
            } else if (newPassword.length() > 20) {
                return ServerErrors.PASSWORD_TOO_LONG.getErrorDescrition();
            }

        } else {
            return ServerErrors.WRONG_PASSWORD.getErrorDescrition();
        }
        return Statuses.ERROR.name();
    }

    public List<String> channelsByStatus(@NotNull  String userStatus){
        List<Channel> channels=channelDAO.findAll();
        List<Channel> filteredChannels=channelDAO.findAll();
        if (!UserRoles.ADMIN.getAuthority().equals(userStatus) && !UserRoles.MODERATOR.getAuthority().equals(userStatus)){
            filteredChannels.removeAll(channels.stream().filter(channel -> !userStatus.equals(channel.getAllowedStatus())).collect(Collectors.toList()));
            if (!filteredChannels.containsAll(channels.stream().
                    filter(channel -> channel.getAllowedStatus().equals(UserRoles.USER.getAuthority()))
                    .collect(Collectors.toList())))
                filteredChannels.addAll(channels.stream()
                        .filter(channel -> channel.getAllowedStatus().equals(UserRoles.USER.getAuthority()))
                        .collect(Collectors.toList()));
        }
        List<String> channelNames=new ArrayList<>();
        for (Channel channel : filteredChannels){
            channelNames.add(channel.getChannelName());
        }
        return channelNames;
    }

    public Map<String,String> enterChannel(@NotNull String username, @NotNull String channelName) {
        User enterredUser = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        exitChannel(username);
        Channel channel = channelDAO.findByChannelName(channelName).orElse(null);
        channel.addUser(enterredUser);
        channelDAO.save(channel);
        setCurrentChannel(channel);
        Message message=Message.builder()
                .postTime(DateTimeUtil.getCurrentDate())
                .messageText(" entered the "+channelName)
                .messageStatus(MessageStatuses.SYSTEM.getStatusName())
                .user(username).build();
        if (messagesOnChannel.get(channelName)==null){
            List <Message> messages=new ArrayList<>();
            messagesOnChannel.put(channelName,messages);
        }
        messagesOnChannel.get(channelName).add(message);
        return channelDetails(channelName);
    }

    public Map<String, String> channelDetails(@NotNull String channelName){
        Channel channel=channelDAO.findByChannelName(channelName).orElse(null);
        Map<String,String> channelDetails=new HashMap<>();
        channelDetails.put("channelName",channel.toString());
        channelDetails.put("description",channel.getDescription());
        channelDetails.put("allowedStatus",channel.getAllowedStatus());
        channelDetails.put("usersCount",Integer.toString(channel.getUsersCount()));
        return channelDetails;
    }
    public String exitChannel(@NotNull String username){
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        List<Channel> channels=channelDAO.findAll();
        for (Channel channel :channels){
            if (channel.getUsers().contains(user)){
                channel.removeUser(user);
                Message message=Message.builder()
                        .postTime(DateTimeUtil.getCurrentDate())
                        .messageText(" exited the "+channel.getChannelName())
                        .messageStatus(MessageStatuses.SYSTEM.getStatusName())
                        .user(username).build();
                if (messagesOnChannel.get(channel.getChannelName())==null){
                    List <Message> messages=new ArrayList<>();
                    messagesOnChannel.put(channel.getChannelName(),messages);
                }
                messagesOnChannel.get(channel.getChannelName()).add(message);
                channelDAO.save(channel);
            }
        }
        return Statuses.SUCCESS.name();
    }

    public List<String> usersOnChannel(@NotNull String channelName){
        Channel channel =channelDAO.findByChannelName(channelName).orElse(null);
        if (channel!=null){
            List<String> usersOnChannel=new ArrayList<>();
            for (User user : channel.getUsers())
                usersOnChannel.add(user.getUsername());
            return usersOnChannel;
        }
        return null;
    }

    public String checkUserServerStatus(@NotNull String username){
        User user=userDAO.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user " + username + " was not found"));
        if ( RegistrationStatuses.BANNED.name().equals(user.getRegistrationStatus()) ){
            return UserServerStatus.BAN.getUserServerStatus();
        } else if (RegistrationStatuses.CLOSED.equals((user.getRegistrationStatus()))){
            return  UserServerStatus.CLOSE.getUserServerStatus();
        } else {
            return UserServerStatus.OK.getUserServerStatus();
        }
    }

    public String changeStatus(@NotNull String username, String status, String statusChangeCause){
        User user=userDAO.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user " + username + " was not found"));
        exitChannel(username);
        if (user.getAuthorities().size()==1)
            user.getAuthorities().add(UserRoles.getRole(status));
        else if ((user.getAuthorities().contains(UserRoles.VIP) || user.getAuthorities().contains(UserRoles.MODERATOR))
                && !UserRoles.USER.getAuthority().equals(status)){
            user.getAuthorities().remove(user.getAuthorities().get(user.getAuthorities().size()-1));
            user.getAuthorities().add(UserRoles.getRole(status));
        } else{
            user.getAuthorities().remove(user.getAuthorities().get(user.getAuthorities().size()-1));
        }
        user.setStatusChangeCause(statusChangeCause);
        userDAO.save(user);

        return Statuses.SUCCESS.name();

    }

    public String getUserStatus(String username){
        User user=userDAO.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user " + username + " was not found"));
        return user.getAuthorities().get(user.getAuthorities().size()-1).getAuthority();
    }

    public List<String> messagesOnChannel(String channel,String username){
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " was not found"));
        List<Message> filteredMessages;
        if (messagesOnChannel.get(channel)==null)
            filteredMessages=new ArrayList<>();
        else
            filteredMessages= messagesOnChannel.get(channel);
        long minute=1000*60;
        filteredMessages.
                removeAll(filteredMessages.stream().
                        filter(message -> message.getPostTime().before(DateTimeUtil.minus(user.getLoginTime(),minute)))
                        .collect(Collectors.toList()));
        List<String> currentMessages=new ArrayList<>();
        for (Message message :filteredMessages){
            if (MessageStatuses.NORMAL.equals(message.getMessageStatus())){
                String messageText=message.getMessageText()+"\t[ "+DateTimeUtil.convertTimeToString(message.getPostTime())+" ]\n";
                String messageUser=message.getUser();
                String currentMessage=messageUser+": "+messageText;
                currentMessages.add(currentMessage);
            } else {
                String messageText=message.getMessageText()+"\t[ "+DateTimeUtil.convertTimeToString(message.getPostTime())+" ]\n";
                String messageUser=message.getUser();
                String currentMessage=messageUser+" "+messageText;
                currentMessages.add(currentMessage);
            }

        }
        return currentMessages;
    }


    public String postMessage(String username, String channel, String messageText){
        Message message=Message.builder()
                .postTime(DateTimeUtil.getCurrentDate())
                .messageText(messageText)
                .messageStatus(MessageStatuses.NORMAL.getStatusName())
                .user(username).build();
        if (messagesOnChannel.get(channel)==null){
            List <Message> messages=new ArrayList<>();
            messagesOnChannel.put(channel,messages);
        }
        messagesOnChannel.get(channel).add(message);
        return Statuses.SUCCESS.name();
    }

    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }
}