package org.vkalashnykov.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails{

    private ObjectId id;

    @Length(min = 6)
    @Indexed(unique = true)
    private String username;
    @Length(min=6)
    @NotNull
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
    private List<UserRoles> authorities;
//    private List<Message> messages;
    @NotEmpty
    private String registrationStatus;
    private Date closeDate;
    private Date lastBanTime;
    private String onlineStatus;


//    public Message addMessage(Message message){
//        if (messages==null){
//            messages=new ArrayList<Message>();
//        }
//        messages.add(message);
//        return message;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return !RegistrationStatuses.CLOSED.equals(registrationStatus) ? true : false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !RegistrationStatuses.CLOSED.equals(registrationStatus) && !RegistrationStatuses.BANNED.equals(registrationStatus)
                ? true : false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !RegistrationStatuses.CLOSED.equals(registrationStatus) ? true : false;
    }

    @Override
    public boolean isEnabled() {
        return !RegistrationStatuses.CLOSED.equals(registrationStatus) ? true : false;
    }


}
