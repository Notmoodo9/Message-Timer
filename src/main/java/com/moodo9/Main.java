package com.moodo9;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MOD_ID = "message-timer";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static double BLOCKS_A_SECOND = 343; // actual speed of sound might want to lessen it
	public static double DIMENSION_TIME_DELAY = 20; // The time delay between Dimensions. in seconds

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!");

		DelayedActionScheduler.init();
	}

	public static void setBlocksASecond(double blocksASecond){
		BLOCKS_A_SECOND = blocksASecond;
	}

	public static double getBlocksASecond(){
		return BLOCKS_A_SECOND;
	}


}