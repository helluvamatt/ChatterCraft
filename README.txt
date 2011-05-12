ChatterCraftPlugin
Matt Schneeberger -- SCHNEENET Internet Services

This plugin is being provided AS-IS with NO WARRANTY of feasability or usability for a particular purpose.
The plugin and all source code is licensed under the terms of the GNU General Public License.
Source included in jar file.

This is a plugin for Bukkit. It enables location tracking of users and chatting with users based on a REST based protocol. It is based on a basic concept and some code from:

Minequery v1.2
http://forum.hey0.net/showthread.php?tid=2983

Install by added the JAR file to your plugins directory

You can access the server by connecting a socket to the port defined in the chattercraft.properties file. (Default is 25566) Once the socket is connected, write a basic string to the socket followed by a newline character. (\n) The string should be one of the following:

LOGIN <name of chat user>:<unique id>
CHATTER <name of chat user>:<unique id>:<message>
QUERY_XML <name of chat user>:<unique id>:<timestamp of last request>

<unique id> is a Unique identifing string for each user. Typically the PHP REMOTE_ADDR will suffice.
<timestamp of last request> should be the number returned from the last request; QUERY will give this value

Sending the newline character (\n) will dump valid XML for the response and immediately terminate the connection.

Minequery compatibility is mantained with the following commands:
QUERY
QUERY_JSON 

PLEASE NOTE:
I wrote this plugin specifically for my own use and am releasing it so other users can benefit from it. However, due to the fact that I am a full time college student, I can not and WILL NOT support this plugin. If something is broken, I can fix it (because that mean my copy for my server is broken as well). This will be subject to when I have time for it! I have included a PHP script that I use for my website, and I believe it a good idea to hide the plugin's server behind a firewall and use the PHP script to interface with the server. The PHP script is also GPL.

MINECRAFT OVERVIEWER ADD ON
1. Add the 'chattercraft' directory to your 'web_assets' folder in the Minecraft Overviewer install or add it to the output folder after the rendering is finished.
2. Add the following code to the <head> of the 'index.html' file in the output folder or 'web_assets' folder:

<!-- ChatterCraft --><link rel="stylesheet" href="chattercraft/chattercraft.css" type="text/css" /> 
<!-- ChatterCraft --><script type="text/javascript" src="chattercraft/chattercraft.js"></script>

3. Add the following code to the <body> of the 'index.html' file in the output folder or 'web_assets' folder:

<!-- ChatterCraft start --> 
<div id="chattercraft"> 
	<div id="chattercraft_loginwin"> 
		<label for="chattercraft_username_field">Username:</label> 
		<input type="text" id="chattercraft_username_field" /> 
		<a href="Javascript:;" onclick="chattercraft.set_username();" class="image-button">Login</a> 
	</div> 
	<div id="chattercraft_chatwin"> 
		<div id="chattercraft_status_message"></div> 
		<div id="chattercraft_messages"></div> 
		<div id="chattercraft_userlist"></div> 
	</div> 
	<div id="chattercraft_sendform"> 
		<input type="text" id="chattercraft_sendform-msg" name="msg" /> 
		<a href="Javascript:;" onclick="chattercraft.send_message();" class="image-button" id="chattercraft_send_button" >Send</a> 
	</div> 
</div>
<script type="text/javascript">
// Initialize ChatterCraft
chattercraft.init();
</script>
<!-- ChatterCraft end -->