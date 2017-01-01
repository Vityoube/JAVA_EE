package org.vkalashnykov.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkalashnykov on 01.01.17.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "channels")
public class Channel {
    private ObjectId id;
    private String channelName;
    private String description;
    private String allowedStatus;
    private List<User> users=new ArrayList<>();
    private int usersCount=0;



    public User addUser(User user){
        if (users==null)
            users=new ArrayList<>();
        users.add(user);
        usersCount++;
        return user;
    }

    @Override
    public String toString() {
        return channelName;
    }

    public User removeUser(User user){
        if(user==null)
            return null;
        users.remove(user);
        usersCount--;
        return user;
    }
}
