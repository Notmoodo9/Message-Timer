package com.moodo9;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DelayedActionScheduler {
    private static final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    // Initialize the tick scheduler
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(DelayedActionScheduler::onTick);
    }

    // This method is called every tick
    private static void onTick(MinecraftServer server) {
        Iterator<ScheduledTask> iterator = scheduledTasks.iterator();

        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            task.ticksLeft--;

            if (task.ticksLeft <= 0) {
                // Execute the task
                task.runnable.run();
                // Remove it from the list after execution
                iterator.remove();
            }
        }
    }

    // Method to schedule a task with a delay (in ticks)
    public static void scheduleTask(int delayInTicks, Runnable runnable) {
        scheduledTasks.add(new ScheduledTask(delayInTicks, runnable));
    }

    // Inner class to store a task with a delay
    private static class ScheduledTask {
        int ticksLeft;
        Runnable runnable;

        public ScheduledTask(int ticksLeft, Runnable runnable) {
            this.ticksLeft = ticksLeft;
            this.runnable = runnable;
        }
    }
}

