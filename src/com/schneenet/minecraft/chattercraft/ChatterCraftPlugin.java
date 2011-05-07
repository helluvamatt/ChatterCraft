package com.schneenet.minecraft.chattercraft;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatterCraftPlugin extends JavaPlugin {
	
	private static PluginDescriptionFile description;
	private static ChatterCraftServer server;
	private static ChatterCraftPlayerListener playerListener;
	//private static ChatterCraftListener listener;
	
	// Configuration
	private boolean send_all_chats = true;
	private int port = 25566;
	protected final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		server.shutdown();
		log.info( description.getName() + " version " + description.getVersion() + " is disabled!" );
	}

	@Override
	public void onEnable() {
		description = this.getDescription();
		server = new ChatterCraftServer(port, log, this);
		server.startup();
		playerListener = new ChatterCraftPlayerListener(this);
		//listener = new ChatterCraftListener(this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		//this.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, listener, Priority.Normal, this);
		// TODO Read configuration
		log.info( description.getName() + " version " + description.getVersion() + " is enabled!" );
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
	
	protected boolean getSendAllChats() {
		return this.send_all_chats;
	}
	
	protected static ChatterCraftServer getChatterCraftServer() {
		return server;
	}

}
