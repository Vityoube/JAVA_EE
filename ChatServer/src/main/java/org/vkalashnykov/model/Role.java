package org.vkalashnykov.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkalashnykov on 04.12.16.
 */
@Data
public class Role {
    private String id;
    private String name;
    private List<User> users;

    public Role(String name){
        this.name=name;
    }

    public User addUser(User user){
        if (users==null){
            users=new ArrayList<>();
        }
        users.add(user);
        return user;
    }
}
