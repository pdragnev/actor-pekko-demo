package com.actors.sharding.demo.actors.state;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jacob.falzon
 */
@Builder
@Data
public class PlayerState {
    
    private String id;
    private long balance;
    private LocalDateTime creationTime;
    
}
