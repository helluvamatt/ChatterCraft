import java.util.Iterator;
import java.util.logging.Logger;

/*
 * JabberCraft
 */

public class ChatterCraftListener extends PluginListener  {
	
	Logger logger;
	ChatterCraftServer server;
	
	// Configuration
	private boolean enabled;
	private boolean send_all_chats;
	private boolean show_ingame_group;
	private int port;
	

	public ChatterCraftListener() {
		enabled = false;
		send_all_chats = false;
		port = 0;
	}
	
	void enable() {
		enabled = true;
		// Read config file for server
		PropertiesFile pf = new PropertiesFile("chattercraft.properties");
		send_all_chats = pf.getBoolean("send_all_chats", false);
		show_ingame_group = pf.getBoolean("show_ingame_group", false);
		port = pf.getInt("port", 25566);
		server = new ChatterCraftServer(port, this.logger);
		server.startup();
	}
	
	void disable() {
		enabled = false;
		server.shutdown();
	}
	
	public boolean onChat(Player player, String msg) {
		if (enabled && send_all_chats) {
			// Send chat message to external chat clients
			//TODO I would love for someone to tell me how to find the most priveledged group this user belongs to. See the getHighestGroup method for my best effort.
			server.postMessage(ChatterMessage.createMessage(player.getName() + (show_ingame_group ? " (" + player.getGroups()[0] + ")" : ""), "player", msg));
		}
		// Still send messages to ingame clients
		return false;
	}
	
	public boolean onCommand(Player player, String[] split) {
		if (split[0].equalsIgnoreCase(ChatterCraftServer.USER_LIST_COMMAND)) {
			if (player.canUseCommand(ChatterCraftServer.USER_LIST_COMMAND)) {
				player.sendMessage(Colors.LightBlue + "Online WWW Users:");
				Iterator<ChatterUser> users = server.getWWWUsers().iterator();
				while (users.hasNext()) {
					ChatterUser u = users.next();
					player.sendMessage(Colors.White + "   " + u.getUsername());
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Find the Group that is at the top of the inheritance stack for a Player
	 * @param p Player
	 * @return Group
	 */
	public static Group getHighestGroup(Player p) {
		String[] gs = p.getGroups();
		Group highest = etc.getInstance().getDefaultGroup();
		
		// Iterate over each group the user explicitly belongs to.
		for (String g : gs) {
			// Get a Group object for this group
			Group temp = etc.getDataSource().getGroup(g);
			// Iterator over each group that this group inherits
			for (String g2 : temp.InheritedGroups) {
				// If the current highest group is inherited by this group, it is the new highest.
				if (highest.Name.equals(g2)) highest = temp;
			}
		}
		return highest;
	}
	
}
