package org.vkalashnykov.service.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vkalashnykov.model.Message;
import org.vkalashnykov.model.Role;
import org.vkalashnykov.model.User;
import org.vkalashnykov.persistence.RoleRepository;
import org.vkalashnykov.persistence.UserRepository;
import org.vkalashnykov.service.spring.interfaces.UserServiceInteface;

import javax.xml.ws.WebServiceException;
import java.util.Date;
import java.util.List;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Service
public class UserService implements UserServiceInteface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    private String currentUserUsername;

    private Date unblockTime;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void insert(User user) {
        userRepository.insert(user);
    }

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.addRole(roleRepository.findByName(User.UserRoles.User));
        userRepository.save(user);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Role findRoleById(String id) {
        return roleRepository.findById(id);
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void insertRole(Role role) {
        roleRepository.insert(role);
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public List<Message> getMessages(User user) {
        return user.getMessages();
    }


    public Message sendMessage(String messageText, String username) {
        User userTo = userRepository.findByUsername(username);
        User currentUser = userRepository.findByUsername(currentUserUsername);
        Message message = new Message(currentUser,userTo,messageText);
        return message;
    }

    public void addContact(String contactUsername) {
        User contact = userRepository.findByUsername(contactUsername);
        User user = userRepository.findByUsername(currentUserUsername);
        user.addContact(contact);
    }

    public void removeContact(String contactUsername) {
        User contact = userRepository.findByUsername(contactUsername);
        User user = userRepository.findByUsername(currentUserUsername);
        user.addContact(contact);
    }

    @CacheEvict("messagesCache")
    public void closeUser(String username) {
        User user = userRepository.findByUsername(username);
        user.setRegistrationStatus(User.RegistrationStatuses.CLOSED);
        user.setCloseDate(new Date());
    }

    public void banUser(String username, long banTime) {
        User user = userRepository.findByUsername(username);
        user.setRegistrationStatus(User.RegistrationStatuses.BANNED);
        user.setLastBanTime(new Date());
        unblockTime = new Date(user.getLastBanTime().getTime() + banTime);
    }

    public void createUser(String username, String password) {
        User user = new User(username, password);
        user.setRegistrationStatus(User.RegistrationStatuses.PENDING);
        user.addRole(roleRepository.findByName(User.UserRoles.User));
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
