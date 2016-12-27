package org.vkalashnykov.service;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vkalashnykov.configuration.Statuses;
import org.vkalashnykov.model.RegistrationStatuses;
import org.vkalashnykov.model.User;
import org.vkalashnykov.model.UserRoles;
import org.vkalashnykov.persistence.UserDAO;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.xml.ws.WebServiceException;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserDAO userDAO;


    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String currentUserUsername;

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

    @Autowired
    public BCryptPasswordEncoder getbCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
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

    public void createUser(String username, String password) {
        try {
            userDAO.save(User.builder()
                    .username(username)
                    .password(new BCryptPasswordEncoder().encode(password))
                    .registrationStatus(RegistrationStatuses.PENDING.name())
                    .authorities(ImmutableList.of(UserRoles.USER))
                    .registrationDate(new Date())
                    .build());
        } catch (Exception e){
            throw new WebServiceException("Couldn't create user",e);
        }

    }

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        return userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user "+username+ " was not found"));  // Here the userDAO is null
    }

    public String login(@NotNull String username, @NotNull String password){
        User user=(User)loadUserByUsername(username);
        if (user!=null && user.getPassword().equals(new BCryptPasswordEncoder().encode(password)))
            return Statuses.SUCCESS.name();
        else
            return Statuses.ERROR.name();
    }
}
