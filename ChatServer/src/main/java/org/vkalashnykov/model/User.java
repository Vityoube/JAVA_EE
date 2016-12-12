package org.vkalashnykov.model;

import lombok.Data;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Data
public class User {

    public interface UserRoles {
        final static String ADMIN = "A";
        final static String User = "U";
    }

    public interface RegistrationStatuses {
        final static String REGISTERED = "R";
        final static String PENDING = "P";
        final static String BANNED = "B";
        final static String CLOSED = "C";
    }

    public interface OnlineStaruses {
        final static String ONLINE="O";
        final static String AWAY="A";
        final static String DONT_DISTURB="B";
        final static String INVISIBLE="I";
        final static String NOT_ACTIVE="N";
    }
    private String id;
    @Length(min = 6)
    private String username;
    private String password;
    @Transient
    private String passwordConfirm;
    private String firstName;
    private String lastName;
    @Min(value = 1)
    private int age;
    @NotEmpty
    private Date registrationDate;
    @NotEmpty
    private Date lastLoginDate;
    @NotEmpty
    private List<Role> userRoles;
    private List<User> contacts;
    private List<Message> messages;
    @NotEmpty
    private String registrationStatus;
    private Date closeDate;
    private Date lastBanTime;
    private String onlineStatus;

    public User(){

    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User addContact(User contact) {
        if (contacts == null) {
            contacts = new ArrayList<User>();
        }
        contacts.add(contact);
        return contact;
    }

    public User removeContact(User contact) throws ServletWebServer.Exception {
        if (contacts == null) {
            contacts = new ArrayList<User>();
        }
        if (contacts.contains(contact)) {
            contacts.remove(contact);
        } else {
            throw new ServletWebServer.Exception(404, "Contact not found", "User isn't in contacts");
        }
        return contact;
    }

    public Message addMessage(Message message){
        if (messages==null){
            messages=new ArrayList<Message>();
        }
        messages.add(message);
        return message;
    }

    public Role addRole(Role role){
        if (userRoles==null){
            userRoles=new ArrayList<>();
        }
        userRoles.add(role);
        role.addUser(this);
        return role;
    }
}
