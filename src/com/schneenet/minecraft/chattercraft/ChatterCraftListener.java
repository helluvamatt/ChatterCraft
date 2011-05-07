package com.schneenet.minecraft.chattercraft;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public class ChatterCraftListener extends CustomEventListener {

	private ChatterCraftPlugin parent;
	
	public ChatterCraftListener(ChatterCraftPlugin parent) {
		this.parent = parent;
	}
	
	public void onCustomEvent(Event event) {
		this.parent.getServer();
	}
	
}
