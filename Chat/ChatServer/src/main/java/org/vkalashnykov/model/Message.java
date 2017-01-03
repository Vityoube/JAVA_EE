package org.vkalashnykov.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;

/**
 * Created by vkalashnykov on 03.12.16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String user;
    private Date postTime;
    private String messageText;
    private String channel;
    private String messageStatus;
}
