package com.schneenet.minecraft.chattercraft;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


/*
 * JabberCraft
 */

public class ChatterCraftPlayerListener extends PlayerListener {
	
	private ChatterCraftPlugin parent;
	
	public ChatterCraftPlayerListener(ChatterCraftPlugin parent) {
		this.parent = parent;
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent e) {
		if (parent.getSendAllChats()) {
			ChatterCraftPlugin.getChatterCraftServer().postMessage(ChatterMessage.createMessage(e.getPlayer().getDisplayName(), "player", e.getMessage()));
		}
	}
	
}
