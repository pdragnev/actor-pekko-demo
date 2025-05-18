package com.actors.sharding.demo;

import static com.actors.sharding.demo.actors.commands.PlayerCommand.DEFAULT_TIMEOUT;

import com.actors.sharding.demo.actors.commands.AdjustBalanceCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.actors.sharding.demo.actors.PlayerActorRefResolver;
import com.actors.sharding.demo.actors.commands.GetPlayerCommand;
import com.actors.sharding.demo.actors.replies.PlayerReply;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RestController} for player operations.
 *
 * @author jacob.falzon
 */
@RestController
@RequestMapping("players")
@RequiredArgsConstructor
@Slf4j
public class PlayerController {
    
    private final PlayerActorRefResolver playerActorRefResolver;
    
    @GetMapping("{id}")
    public PlayerReply getPlayer(@PathVariable("id") final int id) {
        log.info("Received getPlayer Http Request {}", id);
        return playerActorRefResolver.getPlayerActorRef(id)
            .<PlayerReply>askWithStatus(replyTo -> GetPlayerCommand.builder().replyTo(replyTo).build(), DEFAULT_TIMEOUT)
            .toCompletableFuture()
            .join();
    }
    
    @PostMapping("{id}")
    public Void adjustBalance(@PathVariable("id") final int id, // Return CompletionStage
                              @RequestBody final PlayerBalanceAdjustDto request) {
        log.info("Received adjustBalance Http Request for player {} with amount {}", id, request.getAmount());

        return playerActorRefResolver.getPlayerActorRef(id)
                // The AdjustBalanceCommand expects ActorRef<StatusReply<String>>
                .<PlayerReply>askWithStatus(replyTo -> AdjustBalanceCommand.builder()
                                .amount(request.getAmount())
                                .replyTo(replyTo)
                                .build(),
                        DEFAULT_TIMEOUT)
                .thenAccept(statusReply -> {
                    log.info("Balance adjustment successful for player {}: {}", id, statusReply.getBalance());
                })
                .exceptionally(ex -> {
                    log.error("Balance adjustment failed for player {}", id, ex);
                    // You might want to throw a custom exception here
                    throw new RuntimeException("Failed to adjust balance: " + ex.getMessage(), ex);
                })
                .toCompletableFuture()
                .join();
    }
    
    @Data
    @NoArgsConstructor
    public static class PlayerBalanceAdjustDto {
        private int amount;
    }
}
