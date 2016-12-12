package org.vkalashnykov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vkalashnykov.model.User;
import org.vkalashnykov.persistence.UserRepository;

import javax.xml.ws.WebServiceException;
import java.util.Date;
import java.util.List;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private String currentUserLogin;

    private Date unblockTime;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void insert(User user) {
        userRepository.insert(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Cacheable("messages")
    public List<String> getMessages(User user) {
        return user.getMessages();
    }

    @CachePut(value = "messages")
    public void sendMessage(String message, String login) {
        User userTo = userRepository.findByLogin(login);
        User currentUser = userRepository.findByLogin(currentUserLogin);
        userTo.addMessage(login + ": " + message);
        userTo.addMessage(login + ": " + message);
    }

    public void addContact(String loginContact) {
        User contact = userRepository.findByLogin(loginContact);
        User user = userRepository.findByLogin(currentUserLogin);
        user.addContact(contact);
    }

    public void removeContact(String loginContact) {
        User contact = userRepository.findByLogin(loginContact);
        User user = userRepository.findByLogin(currentUserLogin);
        user.addContact(contact);
    }

    public void closeUser(String login) {
        User user = userRepository.findByLogin(login);
        user.setRegistrationStatus(User.RegistrationStatuses.CLOSED);
        user.setCloseDate(new Date());
    }

    public void banUser(String login, long banTime) {
        User user = userRepository.findByLogin(login);
        user.setRegistrationStatus(User.RegistrationStatuses.BANNED);
        user.setLastBanTime(new Date());
        unblockTime = new Date(user.getLastBanTime().getTime() + banTime);
    }

    public void createUser(String login, char[] password) {
        User user = new User(login, password);
        user.setRegistrationStatus(User.RegistrationStatuses.PENDING);
        user.setUserRole(User.UserRoles.User);
        Date registrationDate = new Date();
        user.setRegistrationDate(registrationDate);
        user.setLastLoginDate(registrationDate);
        try {
            userRepository.insert(user);

        } catch (Exception e){
            userRepository.delete(user);
            throw new WebServiceException("Couldn't create user",e);
        }

    }
}
