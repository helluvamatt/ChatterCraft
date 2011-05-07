package com.schneenet.minecraft.chattercraft;

public class ChatterUser {
	private String _username;
	private String _ip;
	private long lastAccessed;
	public ChatterUser(String user, String ip) {
		this._username = user;
		this._ip = ip;
		this.lastAccessed = System.currentTimeMillis();
	}
	public String getUsername() {
		return this._username;
	}
	public String getIP() {
		return this._ip;
	}
	public void access() {
		this.lastAccessed = System.currentTimeMillis();
	}
	public boolean isOlderThan(long time) {
		return this.lastAccessed < time;
	}
}
