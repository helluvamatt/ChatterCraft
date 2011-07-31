package com.schneenet.minecraft.chattercraft;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
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
	private boolean log_all_chats = true;
	private int port = 25566;
	private boolean notify_signon = true;
	private boolean notify_signoff = true;
	private String chat_tag = "&f<&b[WWW Portal] &c%u&f> ";
	protected final Logger log = Logger.getLogger("Minecraft");
	
	
	@Override
	public void onDisable() {
		server.shutdown();
		log.info( description.getName() + " version " + description.getVersion() + " is disabled!" );
	}

	@Override
	public void onEnable() {
		description = this.getDescription();
		
		// Read configuration
		File configFile = new File(this.getDataFolder(), "config.yml");
		Configuration config = new Configuration(configFile);
		config.load();
		chat_enabled = config.getBoolean("chat_enabled", true);
		chat_tag = config.getString("chat_tag", "&f<&b[WWW Portal] &c%u&f> ");
		log_all_chats = config.getBoolean("log_all_chats", true);
		send_all_chats = config.getBoolean("send_all_chats", true);
		port = config.getInt("port", 25566);
		notify_signon = config.getBoolean("notify.signon", true);
		notify_signoff = config.getBoolean("notify.signoff", true);
		if (!configFile.isFile()) {
			config.save();
		}
		
		// Start the ChatterCraft Server
		server = new ChatterCraftServer(port, log, this);
		server.startup();
		
		// Register chat events
		playerListener = new ChatterCraftPlayerListener(this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		
		// Started!
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
	
	protected boolean getLogAllChats() {
		return this.log_all_chats;
	}
	
	protected boolean getNotifySignOn() {
		return this.notify_signon;
	}
	
	protected boolean getNotifySignOff() {
		return this.notify_signoff;
	}
	
	protected String getChatTag(String username) {
		String tag = "";
		int len = this.chat_tag.length();
		for (int i = 0; i < len; i++) {
			if (chat_tag.charAt(i) == '&' && i < (len - 1) && chat_tag.charAt(i+1) != '&') {
				// Figure out the color code
				int code = Integer.parseInt(String.valueOf(chat_tag.charAt(i+1)), 16);
				
				// Append the color
				tag += ChatColor.getByCode(code);
				
				// Advance the pointer
				i++;
			} else {
				tag += chat_tag.charAt(i);
			}
		}
		return tag.replace("%u", username);
	}
	
	protected static ChatterCraftServer getChatterCraftServer() {
		return server;
	}

}
