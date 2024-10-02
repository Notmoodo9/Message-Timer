package com.moodo9.mixin;

import com.moodo9.DelayedActionScheduler;
import com.moodo9.Main;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler networkHandler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = networkHandler.player;

        // Get the message from the player
        String message = packet.chatMessage();

        // Broadcast the message manually to all players
        broadcastToAllPlayers(player, message);

        // Cancel the normal message handling
        ci.cancel();
    }

    // Helper method to broadcast the message to all players
    @Unique
    private void broadcastToAllPlayers(ServerPlayerEntity sender, String message) {
        // Get the current time in hour:min:sec format
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(timeFormatter);
        String name = sender.getNameForScoreboard();

        Text formattedMessage = Text.of(  (name + " ["+formattedTime+"] " + message));

        Main.LOGGER.info(formattedMessage.getString());

        // Get the world and player location
        World world = sender.getWorld();
        Vec3d senderPosition = sender.getPos();

        double blocksASecond = Main.getBlocksASecond();

        // Loop through all online players and send the message
        for (ServerPlayerEntity player : Objects.requireNonNull(sender.getServer()).getPlayerManager().getPlayerList()) {
            if(player.equals(sender)){
                player.sendMessage(formattedMessage, false);
                continue;
            }

            World playerW = player.getWorld();
            Vec3d playerPosition = player.getPos();

            double distance = senderPosition.distanceTo(playerPosition);

            // Calculate the time in seconds it will take to "travel" the distance
            double timeInSeconds = 0;

            if (world.getRegistryKey() == World.NETHER) {
                if(playerW.getRegistryKey() == World.NETHER) {
                    timeInSeconds = distance / blocksASecond;
                } else {
                    timeInSeconds = (distance * 8) / blocksASecond;
                }
            } else {
                if(playerW.getRegistryKey() == World.NETHER) {
                    timeInSeconds = (distance/8) / blocksASecond;
                } else {
                    timeInSeconds = distance / blocksASecond;
                }
            }

            if(world.getRegistryKey() != playerW.getRegistryKey()){
                timeInSeconds += Main.DIMENSION_TIME_DELAY;
            }

            Main.LOGGER.info("TEST");

            scheduleDelayedAction(player, timeInSeconds, formattedMessage);
        }
    }

     @Unique
     void scheduleDelayedAction(ServerPlayerEntity player, double delay, Text message) {
         int delayInTicks = (int) (delay * 20);

         Main.LOGGER.info("delay: " + delayInTicks);

         // Schedule the task using the custom scheduler
         DelayedActionScheduler.scheduleTask(delayInTicks, () -> {
             // Send the message to the player after the delay
             player.sendMessage(message, false);
         });
    }
}
