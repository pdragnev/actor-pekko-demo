package com.actors.sharding.demo.actors.commands;

import com.actors.sharding.demo.actors.replies.PlayerReply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.pattern.StatusReply;
import com.fasterxml.jackson.annotation.JsonCreator;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class AdjustBalanceCommand implements PlayerCommand {
    private int amount;
    private ActorRef<StatusReply<PlayerReply>> replyTo;
}