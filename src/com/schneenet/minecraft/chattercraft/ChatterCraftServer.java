package com.schneenet.minecraft.chattercraft;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatterCraftServer extends Thread {

	public static final String USER_LIST_COMMAND = "wwwlist";

	/**
	 * The query server socket.
	 */
	public ServerSocket serverSocket;

	private int port;
	private ArrayList<ChatterMessage> messages;
	private ArrayList<ChatterUser> onlineUsers;
	private Logger logger;
	private ChatterCraftPlugin plugin;

	/**
	 * Constructs the query server.
	 * 
	 * @param port
	 *            The port the query server should run on.
	 * @param l
	 *            Logger object the server should use
	 */
	public ChatterCraftServer(int port, Logger l, ChatterCraftPlugin p) {
		this.port = port;
		this.logger = l;
		this.plugin = p;
		messages = new ArrayList<ChatterMessage>();
		onlineUsers = new ArrayList<ChatterUser>();

	}

	public void startup() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				Iterator<ChatterUser> iter = onlineUsers.iterator();
				while (iter.hasNext()) {
					ChatterUser user = iter.next();
					if (user.isOlderThan(System.currentTimeMillis() - 3000)) {
						if (plugin.getNotifySignOff()) {
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + user.getUsername() + " [WWW User] has disconnected from chat.");
						}
						iter.remove();
					}
				}
			}
		}, 0, 1000);

		// Start listening
		this.start();
	}

	public void shutdown() {
		plugin.getServer().getScheduler().cancelTasks(plugin);
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
			} // Ignore
		}
	}

	public ArrayList<ChatterMessage> getMessages() {
		synchronized (messages) {
			return messages;
		}
	}

	public ArrayList<ChatterUser> getWWWUsers() {
		synchronized (onlineUsers) {
			return onlineUsers;
		}
	}

	public void postMessage(ChatterMessage msg) {
		synchronized (messages) {
			messages.add(msg);
		}
	}

	/**
	 * The query plugin.getServer() thread.
	 */
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			while (!serverSocket.isClosed()) {

				Socket connectionSocket = serverSocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String in = inFromClient.readLine();

				if (in != null) {
					String[] split = in.split("\\s+", 2);
					if (split[0].equalsIgnoreCase("QUERY")) {
						String[] playerList = new String[plugin.getServer().getOnlinePlayers().length];
						for (int i = 0; i < plugin.getServer().getOnlinePlayers().length; i++) {
							playerList[i] = plugin.getServer().getOnlinePlayers()[i].getName();
						}

						// Build the response.
						StringBuilder resp = new StringBuilder();
						resp.append("SERVERPORT " + plugin.getServer().getPort() + "\n");
						resp.append("PLAYERCOUNT " + plugin.getServer().getOnlinePlayers().length + "\n");
						resp.append("MAXPLAYERS " + plugin.getServer().getMaxPlayers() + "\n");
						resp.append("PLAYERLIST " + Arrays.toString(playerList) + "\n");

						// Send the response.
						outToClient.writeBytes(resp.toString());

						// Handle a request, respond in JSON format.
					} else if (split[0].equalsIgnoreCase("QUERY_JSON")) {
						// Build the JSON response.
						StringBuilder resp = new StringBuilder();
						resp.append("{");
						resp.append("\"serverPort\":").append(plugin.getServer().getPort()).append(",");
						resp.append("\"playerCount\":").append(plugin.getServer().getOnlinePlayers().length).append(",");
						resp.append("\"maxPlayers\":").append(plugin.getServer().getMaxPlayers()).append(",");
						resp.append("\"playerList\":");
						resp.append("[");
						// Iterate through the players.
						int count = 0;
						for (Player player : plugin.getServer().getOnlinePlayers()) {
							resp.append("\"" + player.getName() + "\"");
							if (++count < plugin.getServer().getOnlinePlayers().length) {
								resp.append(",");
							}
						}
						resp.append("]");
						resp.append("}\n");
						// Send the JSON response.
						outToClient.writeBytes(resp.toString());
					} else if (split[0].equalsIgnoreCase("QUERY_XML")) {

						long since = 0;
						String username = "";
						// User provided login?
						if (split.length > 1 && !split[1].isEmpty()) {
							String[] args = split[1].split("\\:", 3);
							String un = args[0];
							String ip = args[1];
							Iterator<ChatterUser> iter = onlineUsers.iterator();
							while (iter.hasNext()) {
								ChatterUser user = iter.next();
								if (user.getUsername().equals(un) && user.getIP().equals(ip)) {
									user.access();
									username = un;
									break;
								}
							}
							since = args.length > 1 ? Long.parseLong(args[2]) : 0;
						}

						int serverPort = plugin.getServer().getPort();
						int playerCount = plugin.getServer().getOnlinePlayers().length;
						int maxPlayers = plugin.getServer().getMaxPlayers();
						StringBuffer sb = new StringBuffer();
						sb.append("<mcserver port=\"");
						sb.append(serverPort);
						sb.append("\" user_count=\"");
						sb.append(playerCount);
						sb.append("\" max_users=\"");
						sb.append(maxPlayers);
						sb.append("\">\n");
						sb.append("<playerlist>\n");
						for (int i = 0; i < playerCount; i++) {
							Player p = plugin.getServer().getOnlinePlayers()[i];
							sb.append("<player x=\"");
							sb.append(p.getLocation().getX());
							sb.append("\" y=\"");
							sb.append(p.getLocation().getY());
							sb.append("\" z=\"");
							sb.append(p.getLocation().getZ());
							sb.append("\">");
							sb.append(p.getName());
							sb.append("</player>\n");
						}
						Iterator<ChatterUser> iter = onlineUsers.iterator();
						while (iter.hasNext()) {
							ChatterUser user = iter.next();
							if (user.isOlderThan(System.currentTimeMillis() - 3000)) {
								if (plugin.getNotifySignOff()) {
									plugin.getServer().broadcastMessage(ChatColor.YELLOW + user.getUsername() + " [WWW User] has disconnected from chat.");
								}
								iter.remove();
							} else {
								sb.append("<user>");
								sb.append(user.getUsername());
								sb.append("</user>");
							}
						}
						sb.append("</playerlist>\n");

						// Chatter
						if (plugin.getChatEnabled()) {
							if (!username.isEmpty()) {
								long now = System.currentTimeMillis();
								if (since != 0) {
									if (now > since) {
										sb.append("<chatter now=\"");
										sb.append(now);
										sb.append("\" since=\"");
										sb.append(since);
										sb.append("\">\n");
										synchronized (messages) {
											Iterator<ChatterMessage> iter2 = messages.iterator();
											while (iter2.hasNext()) {
												ChatterMessage msg = iter2.next();
												if (msg.getTimestamp() > since) {
													sb.append("<message timestamp=\"");
													sb.append(msg.getTimestamp());
													sb.append("\" player=\"");
													sb.append(msg.getUsername());
													sb.append("\" type=\"");
													sb.append(msg.getUsertype());
													sb.append("\">");
													sb.append(msg.getMessage());
													sb.append("</message>\n");
												}
											}
										}
										sb.append("</chatter>\n");
									} else {
										sb.append("<error>Invalid timestamp.</error>\n");
									}
								} else {
									sb.append("<chatter now=\"");
									sb.append(now);
									sb.append("\" />\n");
								}
							} else {
								sb.append("<error>Please login.</error>\n");
							}
						}
						sb.append("</mcserver>\n");
						try {
							outToClient.writeBytes(sb.toString());
						} catch (IOException ignored) {
						}
					} else if (split[0].equalsIgnoreCase("CHATTER")) {
						StringBuffer sb = new StringBuffer();
						if (plugin.getChatEnabled()) {
							if (split.length > 1 && !split[1].isEmpty()) {
								// Parse the argument into username and message
								String[] args = split[1].split("\\:", 3);
								if (args.length > 2) {
									String un = args[0];
									String ip = args[1];
									Iterator<ChatterUser> iter = onlineUsers.iterator();
									boolean found = false;
									while (iter.hasNext()) {
										ChatterUser user = iter.next();
										if (user.getUsername().equals(un) && user.getIP().equals(ip)) {
											user.access();
											plugin.getServer().broadcastMessage(plugin.getChatTag(un) + args[2]);
											synchronized (messages) {
												messages.add(ChatterMessage.createMessage(args[0] + " (WWW Portal)", "user", args[2]));
											}
											sb.append("<success>Message sent.</success>\n");
											found = true;
											break;
										}
									}
									if (!found) {
										sb.append("<error>User not found.</error>\n");
									}
								} else {
									sb.append("<error>Missing argument(s).</error>\n");
								}
							} else {
								sb.append("<error>Missing argument.</error>\n");
							}
						} else {
							sb.append("<error>Chat is disabled.</error>\n");
						}
						try {
							outToClient.writeBytes(sb.toString());
						} catch (IOException ex) {
						}
					} else if (split[0].equalsIgnoreCase("LOGIN")) {
						StringBuffer sb = new StringBuffer();
						if (plugin.getChatEnabled()) {
							if (split.length > 1 && !split[1].isEmpty()) {
								// Parse the arguments into username and ip
								String[] args = split[1].split("\\:", 2);
								if (args.length > 1) {
									String un = args[0];
									String ip = args[1];
									if (!un.isEmpty()) {
										// Check if the username is in the
										// database
										boolean found = false;
										Iterator<ChatterUser> iter = onlineUsers.iterator();
										while (iter.hasNext()) {
											ChatterUser user = iter.next();
											if (user.getUsername().equals(un)) {
												found = true;
												break;
											}
										}
										if (!found) {
											onlineUsers.add(new ChatterUser(un, ip));
											sb.append("<success>Login successful.</success>\n");
											if (plugin.getNotifySignOn()) {
												plugin.getServer().broadcastMessage(ChatColor.YELLOW + un + " [WWW User] has logged in to chat.");
											}
										} else {
											sb.append("<error>Login failed. The username is already in use.</error>\n");
										}
									} else {
										sb.append("<error>Login failed. Invalid username.</error>\n");
									}
								} else {
									sb.append("<error>Login failure. Missing argument.</error>\n");
								}
							} else {
								sb.append("<error>Login failure. Missing argument.</error>\n");
							}
						} else {
							sb.append("<error>Chat is disabled!</error>\n");
						}
						outToClient.writeBytes(sb.toString());
					} else {
						String out = "<error>Invalid request.</error>\n";
						outToClient.writeBytes(out);
					}
				}
				connectionSocket.close();
			}
		} catch (IOException e) {
			logger.severe("[ChatterCraft] IOException in server thread: " + e.getMessage());
		}
	}
}
