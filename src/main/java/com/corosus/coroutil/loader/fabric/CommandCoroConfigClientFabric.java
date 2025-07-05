package com.corosus.coroutil.loader.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("unused")
public class CommandCoroConfigClientFabric {
	public static void register(final CommandDispatcher<FabricClientCommandSource> dispatcher) {
	}

	public static String getCommandName() {
		return "coro";
	}
}
