package org.vkalashnykov.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.vkalashnykov.model.User;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Created by vkalashnykov on 26.11.16.
 */

@Repository
public interface UserDAO extends MongoRepository<User,ObjectId>{

    public Optional<User> findByUsername(@NotNull String username);

    public User save(User user);

}
