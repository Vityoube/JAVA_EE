package org.vkalashnykov.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.vkalashnykov.model.User;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>{
    User findByUsername(String username);
    User findById(String id);
}
