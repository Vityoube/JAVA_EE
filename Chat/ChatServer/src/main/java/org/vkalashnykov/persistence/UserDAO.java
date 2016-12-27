package org.vkalashnykov.persistence;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vkalashnykov.model.User;
import org.vkalashnykov.model.UserField;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by vkalashnykov on 26.11.16.
 */

@Component
public class UserDAO{

    @Autowired
    MongoTemplate mongoTemplate;

    public Optional<User> findByUsername(@NotNull String username){
        return Optional.ofNullable(mongoTemplate.findOne(
                query(
                        where(UserField.USER_NAME.getField()).is(username)),
                        User.class));
    }

    public void save(User user){
        mongoTemplate.save(user);
    }

}
