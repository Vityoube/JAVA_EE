package org.vkalashnykov.model;

import org.springframework.cache.annotation.Cacheable;

/**
 * Created by vkalashnykov on 03.12.16.
 */

public class Message {
    private String messageText;

    private User from;

    private User to;

    public Message() {
    }

    public Message(User from, User to, String messageText){
        this.messageText = messageText;
        this.from=from;
        this.to=to;
        from.addMessage(this);
        to.addMessage(this);
    }
}
