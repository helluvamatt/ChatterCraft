package com.schneenet.minecraft.chattercraft;
/**
 * Message Object Class
 */

public class ChatterMessage {
	protected ChatterMessage(String user, String usertype, String message, long timestamp) {
		this.msg = message;
		this.un = user;
		this.ut = usertype;
		this.ts = timestamp;
	}
	private String msg;
	private String un;
	private String ut;
	private long ts;
	public String getMessage() {
		return msg;
	}
	public String getUsername() {
		return un;
	}
	public String getUsertype() {
		return ut;
	}
	public long getTimestamp() {
		return ts;
	}
	public static ChatterMessage createMessage(String user, String usertype, String text) {
		return new ChatterMessage(user, usertype, text, System.currentTimeMillis());
	}
}