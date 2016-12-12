package org.vkalashnykov.service.spring.interfaces;

import org.springframework.cache.annotation.Cacheable;
import org.vkalashnykov.model.Message;
import org.vkalashnykov.model.User;

import java.util.List;

/**
 * Created by vkalashnykov on 03.12.16.
 */
public interface UserServiceInteface {
    List<User> findAll();
    User findById(String id);
    User findByUsername(String login);
    void insert(User user);
    void save(User user);
    @Cacheable("messagesCache") List<Message> getMessages(User user);
    @Cacheable("messagesCache") Message sendMessage(String messageText, String login);
    void addContact(String loginContact);
    void removeContact(String loginContact);
    void closeUser(String login);
    void banUser(String login,long banTime);
    void createUser(String login,String password);
}
