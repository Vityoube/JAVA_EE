package org.vkalashnykov.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.vkalashnykov.model.Role;

/**
 * Created by vkalashnykov on 04.12.16.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role,String>{
    Role findByName(String name);
    Role findById(String id);
}
