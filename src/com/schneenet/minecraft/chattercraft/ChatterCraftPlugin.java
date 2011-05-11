package com.schneenet.minecraft.chattercraft;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ChatterCraftPlugin extends JavaPlugin {
	
	private static PluginDescriptionFile description;
	private static ChatterCraftServer server;
	private static ChatterCraftPlayerListener playerListener;
	
	// Configuration
	private boolean chat_enabled = true;
	private boolean send_all_chats = true;
	private int port = 25566;
	private boolean notify_signon = true;
	private boolean notify_signoff = true;
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
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		// Read configuration
		File configFile = new File(this.getDataFolder(), "config.yml");
		Configuration config = new Configuration(configFile);
		config.load();
		chat_enabled = config.getBoolean("chat_enabled", true);
		send_all_chats = config.getBoolean("send_all_chats", true);
		port = config.getInt("port", 25566);
		notify_signon = config.getBoolean("notify.signon", true);
		notify_signoff = config.getBoolean("notify.signoff", true);
		log.info( description.getName() + " version " + description.getVersion() + " is enabled!" );
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
	
	protected boolean getChatEnabled() {
		return this.chat_enabled;
	}
	
	protected boolean getSendAllChats() {
		return this.send_all_chats;
	}
	
	protected boolean getNotifySignOn() {
		return this.notify_signon;
	}
	
	protected boolean getNotifySignOff() {
		return this.notify_signoff;
	}
	
	protected static ChatterCraftServer getChatterCraftServer() {
		return server;
	}

}
