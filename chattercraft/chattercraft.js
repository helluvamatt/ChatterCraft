// ChatterCraft Unified JavaScript
var chattercraft = {

	'interval': null,
	'msg_timeout': null,
	'last': 0,
	'username': "",
	'show_player_markers': true, // Show player location markers
	'player_markers': [],
	'perform_query': function() {
		$.ajax({
			type: "POST",
			url: "chattercraft/chatter.php",
			data: {"last": chattercraft.last, "user": chattercraft.username},
			success: function(xmldata) {
				
				// Handle XML Data
				var data = $(xmldata);
				var players = data.find('player');
				var wwwusers = data.find('user');
				var chatter = data.find('chatter');
				
				if (chattercraft.show_player_markers) {
					// UPDATING PLAYER LOCATION MARKERS
					// 1. For Each Marker - Check if it is still needed
					for (var i in chattercraft.player_markers) {
						var p = players.filter(function(j) {
							return $(this).text() == i;
						});
						if (p.length != 1) {
							// 1a. If not delete
							if (chattercraft.player_markers[i] != undefined) {
								chattercraft.player_markers[i].setMap(null);
								chattercraft.player_markers[i] = undefined;
							}
						} else {
							// 1b. Otherwise, update location
							var new_loc = overviewer.util.fromWorldToLatLng(parseFloat(p.attr('x')), parseFloat(p.attr('y')), parseFloat(p.attr('z')));
							var old_loc = player_markers[i].getPosition();
							if (new_loc.lat() != old_loc.lat() || new_loc.lng() != old_loc.lng()) {
								chattercraft.player_markers[i].setPosition(new_loc);
							}
						}
					}
					
					// 2. Add new markers for new players
					players.each(function(i, e) {
						var p = $(e);
						if (chattercraft.player_markers[p.text()] == undefined) {
							// TODO Use this: (maybe) player_avatar/player-avatar.php?player="+p.text()+"&s=1&bc=fff&bw=1&format=flat
							var image = new google.maps.MarkerImage("hiking-tourism.png",
								new google.maps.Size(32.0, 37.0),
								new google.maps.Point(0, 0),
								new google.maps.Point(16.0, 37.0)
							);
							
							// TODO Make a shadow image
							var shadow = new google.maps.MarkerImage("shadow-hiking-tourism.png",
								new google.maps.Size(51.0, 37.0),
								new google.maps.Point(0, 0),
								new google.maps.Point(16.0, 37.0)
							);
							
							chattercraft.player_markers[i] = new google.maps.Marker();
							chattercraft.player_markers[i].setTitle(p.text());
							chattercraft.player_markers[i].setIcon(image);
							chattercraft.player_markers[i].setShadow(shadow);
							chattercraft.player_markers[i].setPosition(overviewer.util.fromWorldToLatLng(parseFloat(p.attr('x')), parseFloat(p.attr('y')), parseFloat(p.attr('z'))));
							chattercraft.player_markers[i].setMap(overviewer.map);
						}
					});
				} else {
					for ( var i in chattercraft.player_markers) {
						if (chattercraft.player_markers[i] != undefined) {
							chattercraft.player_markers[i].setMap(null);
							chattercraft.player_markers[i] = undefined;
						}
					}
				}
				
				// Chat stuff
				if (chatter.length > 0) {
					chattercraft.last = chatter.attr('now');
					var messages = data.find('message');
					if (messages.length > 0) {
						messages.each(function(i, e) {
							$('#chattercraft_messages').append('<div class=\"chattercraft_chat\"><b class=\"' + $(e).attr('type') + '\">' + $(e).attr('player') + '</b>&nbsp;' + $(e).text() + '</div>');
						});
						chattercraft.update_heights();
						$('#chattercraft_messages').attr({scrollTop: $("#chattercraft_messages").attr("scrollHeight") - $('#chattercraft_messages').height()});
					}
				}
				
				// Chat User List
				var userlisthtml = '';
				// INGAME players
				userlisthtml += '<div class="userlist header">Players</div>';;
				if (players.length > 0) {
					players.each(function(i, e) {
						userlisthtml += '<div class="userlist player">' + $(e).text() + '</div>';
					});
				} else {
					userlisthtml += '<div class="userlist generic">No in game players.</div>';
				}
				// WWW users
				userlisthtml += '<div class="userlist header">Chat Users</div>';
				if (wwwusers.length > 0) {
					wwwusers.each(function(i, e) {
						userlisthtml += '<div class="userlist user">' + $(e).text() + '</div>';
					});
				} else {
					userlisthtml += '<div class="userlist generic">No WWW Portal users.</div>';
				}
				$('#chattercraft_userlist').html(userlisthtml);
				
				// Error handling
				var errors = data.find('error');
				if (errors.length > 0) {
					error = errors.first().text();
					chattercraft.show_message('chattercraft_error', error);
				}
			},
			error: function(xhr, error, errno) {
				// Ignore for now
			}
		});
	},

	'show_message': function(msg_class, msg_text) {
		if (chattercraft.msg_timeout != undefined) window.clearTimeout(chattercraft.msg_timeout);
		$('#chattercraft_status_message').text(msg_text);
		$('#chattercraft_status_message').addClass(msg_class);
		$('#chattercraft_status_message').slideDown('fast', function() {chattercraft.update_heights();});
		chattercraft.msg_timeout = window.setTimeout(chattercraft.hide_message, 5000);
	},
	
	'hide_message': function() {
		$('#chattercraft_status_message').slideUp('fast', function() {
			$('#chattercraft_status_message').text('');
			$('#chattercraft_status_message').removeClass('chattercraft_error');
			$('#chattercraft_status_message').removeClass('chattercraft_message');
			chattercraft.update_heights();
		});
	},
	
	'set_username': function() {
		chattercraft.username = $('#chattercraft_username_field').val();
		$.ajax({
			type: "POST",
			url: "chattercraft/chatter.php",
			data: {"action": "ACTION_LOGIN", "user": chattercraft.username},
			success: function(xmldata) {
				var data = $(xmldata);
				if (data.find('success').length > 0) {
					chattercraft.global_error = false;
					chattercraft.show_message('chattercraft_message', "Logged in successfully.");
				} else if (data.find('error').length > 0) {
					chattercraft.global_error = true;
					var error = data.find('error').first().text();
					chattercraft.show_message('chattercraft_error', error);
				}
			},
			error: function(xhr, error, errno) {
				// Ignore, I guess.
			}
		});
	},
	
	'send_message': function() {
		$.ajax({
			type: "POST",
			url: "chattercraft/chatter.php",
			data: {"action": "ACTION_CHATTER", "user": chattercraft.username, "msg": $('#chattercraft_sendform-msg').val()},
			success: function(xmldata) {
				$('.chattercraft_error').remove();
				$('.chattercraft_message').remove();
				$('#chattercraft_sendform-msg').val("");
				var data = $(xmldata);
				if (data.find('success').length > 0) {
					chattercraft.global_error = false;
				} else if (data.find('error').length > 0) {
					chattercraft.global_error = true;
					var error = data.find('error').first().text();
					chattercraft.show_message('chattercraft_error', error);
				}
			},
			error: function(xhr, error, errno) {
				// Ignore, I guess.
			}
		});
	},
	
	'update_heights': function() {
		$('#chattercraft_messages').css('max-height', $('#chattercraft_chatwin').height() - $('#chattercraft_status_message').height()- 1);
		$('#chattercraft_userlist').css('height', $('#chattercraft_chatwin').height() - $('#chattercraft_status_message').height() - 6);
	},
	
	'init': function() {
		
		// Register onresize event
		$(window).bind('resize', chattercraft.update_heights);

		// Enable press enter to login
		$('#chattercraft_username_field').keypress(function(e) {
			if (e.which == '13') {
				e.preventDefault();
				chattercraft.set_username();
			}
		});

		// Enable press enter to send
		$('#chattercraft_sendform-msg').keypress(function(e) {
			if (e.which == '13') {
				e.preventDefault();
				chattercraft.send_message();
			}
		});

		// Query for messages every one second
		chattercraft.interval = window.setInterval(chattercraft.perform_query, 1000);
		
		// Add a custom control for toggling chat
		var chatControlContainer = $('<div id="chatControlContainer" class="control-container" title=\"Chat with players!\"></div>');
		var chatControl          = $('<div id="chatControl" class="control"></div>');
		var chatControlButton    = $('<div id="chatControlButton" class="control-button">Chat</div>');
		chatControl.append(chatControlButton);
		chatControlContainer.append(chatControl);
		chatControlButton.click(function() {
			$(this).toggleClass('control-button-selected');
			$('#chattercraft').slideToggle(400, function() {
				chattercraft.update_heights();
			});
		});
		overviewer.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(chatControlContainer[0]);
	}

}