package org.vkalashnykov.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.vkalashnykov.model.Channel;
import org.vkalashnykov.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Created by vkalashnykov on 01.01.17.
 */
@Repository
public interface ChannelDAO extends MongoRepository<Channel,ObjectId>{
    public Optional<Channel> findByChannelName(@NotNull String channelName);
}
