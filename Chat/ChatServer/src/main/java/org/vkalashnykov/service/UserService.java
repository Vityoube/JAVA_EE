package org.vkalashnykov.service;

import com.google.common.collect.ImmutableList;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vkalashnykov.configuration.ServerErrors;
import org.vkalashnykov.configuration.Statuses;
import org.vkalashnykov.model.OnlineStatuses;
import org.vkalashnykov.model.RegistrationStatuses;
import org.vkalashnykov.model.User;
import org.vkalashnykov.model.UserRoles;
import org.vkalashnykov.persistence.UserDAO;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.xml.ws.WebServiceException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    private Date currentTime;

    private Date unblockTime;

    @PostConstruct
    public void init(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,01,01);
        Date registrationDate =calendar.getTime();
        if (!userDAO.findByUsername("admin").isPresent()){
            userDAO.save(    User.builder()
            .username("admin")
            .password(new BCryptPasswordEncoder().encode("admin"))
            .authorities(ImmutableList.of(UserRoles.ADMIN,UserRoles.USER))
                    .registrationDate(registrationDate)
                    .build());
        }
    }

    //    @Cacheable("messages")
//    public List<Message> getMessages(User user) {
//        return user.getMessages();
//    }
//
//    @CachePut(value = "messages")
//    public void sendMessage(String text, String username) {
//        User userTo = userDAO.findByUsername(username).orElse(null);
//        User currentUser = userDAO.findByUsername(currentUserUsername).orElse(null);
//        Message message = new Message(currentUser, userTo, text);
//        userTo.addMessage(message);
//        userTo.addMessage(message);
//    }

    public void closeUser(String username) {
        User user = userDAO.findByUsername(username).orElse(null);
        user.setRegistrationStatus(RegistrationStatuses.CLOSED.name());
        user.setCloseDate(new Date());
    }

    public void banUser(String username, long banTime) {
        User user = userDAO.findByUsername(username).orElse(null);
        user.setRegistrationStatus(RegistrationStatuses.BANNED.name());
        user.setLastBanTime(new Date());
        unblockTime = new Date(user.getLastBanTime().getTime() + banTime);
    }

    public String createUser(@NotNull String username, @NotNull String password, String name, String lastName, Date birthDate) throws XmlRpcException {
        try {
            int userAge=0;
            if (birthDate!=null){
                currentTime = new Date();
                LocalDate currentTimeLocal = currentTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate userBirthDateLocal = currentTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                userAge= (int)ChronoUnit.YEARS.between(userBirthDateLocal,currentTimeLocal);
                if (!userDAO.findByUsername(username).isPresent()){
            }
                userDAO.save(User.builder()
                        .username(username)
                        .password(new BCryptPasswordEncoder().encode(password))
                        .registrationStatus(RegistrationStatuses.PENDING.name())
                        .authorities(ImmutableList.of(UserRoles.USER))
                        .registrationDate(currentTime)
                        .age(userAge)
                        .onlineStatus(OnlineStatuses.NOT_ACTIVE.name())
                        .birthDate(birthDate)
                        .firstName(name)
                        .lastName(lastName)
                        .build());
                User user=userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user "+username+ " was not found"));
                user.setRegistrationStatus(RegistrationStatuses.REGISTERED.name());
                userDAO.save(user);
                return Statuses.SUCCESS.name();
            } else {
                throw new XmlRpcException(ServerErrors.USER_EXISTS.getUserDescription());
            }


        } catch (Exception e){
            throw new XmlRpcException(e.getMessage());
        }

    }

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        return userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user "+username+ " was not found"));  // Here the userDAO is null
    }



    public String login(@NotNull String username, @NotNull String password){
        try {
            User user = (User) loadUserByUsername(username);
            if (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                user.setOnlineStatus(OnlineStatuses.ONLINE.name());
                userDAO.save(user);
                setCurrentUser(user);
                return Statuses.SUCCESS.name();
            } else
                return Statuses.ERROR.name();
        } catch (UsernameNotFoundException e){
            return Statuses.ERROR.name();
        }
    }
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUserUserName(){
        return currentUser.getUsername();
    }

    public String getCurrentUserOnlineStatus(){
        return currentUser.getOnlineStatus();
    }
}
