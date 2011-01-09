<?PHP
header("Content-type: text/xhtml");

//TODO Handle timeouts for when the server is offline.

$query_server = "localhost";
$port = 25566;

$action = isset($_POST['action']) ? $_POST['action'] : "ACTION_QUERY";
echo "<?xml version=\"1.0\">\n";
echo "<chattercraft>\n";
if ($action == "ACTION_CHATTER") {
	if (isset($_POST['user']) && ($user = $_POST['user']) != "") {
		if (isset($_POST['msg']) && ($message = $_POST['msg']) != "") {
			// Connect and send message
			if ($socket = fsockopen($query_server, $port, $errno, $error, 2)) {
				fwrite($socket, "CHATTER " . $user . ":" . $_SERVER['REMOTE_ADDR'] . ":" . $message . "\r\n");
				while (!feof($socket)) {
					echo fgets($socket, 128);
				}
				fclose($socket);
			} else {
				echo "<error>The server seems to be OFFLINE.</error>\n";
			}	
		} else {
			echo "<error>Cannot send an empty message.</error>\n";
		}
	} else {
		echo "<error>Cannot send a message being logged in.</error>\n";
	}
} else if ($action == "ACTION_LOGIN") {
	$username = isset($_POST['user']) ? $_POST['user'] : "";
	// Connect and login
	if ($socket = fsockopen($query_server, $port, $errno, $error, 2)) {
		fwrite($socket, "LOGIN " . $username . ":" . $_SERVER['REMOTE_ADDR'] . "\r\n");
		while (!feof($socket)) {
			echo fgets($socket, 128);
		}
		fclose($socket);
	} else {
		echo "<error>The server seems to be OFFLINE.</error>\n";
	}
} else {
	$username = isset($_POST['user']) ? $_POST['user'] : "";
	$last = isset($_POST['last']) ? filter_var($_POST['last'], FILTER_SANITIZE_NUMBER_INT) : 0;
	// Connect and query
	if ($socket = fsockopen($query_server, $port, $errno, $error, 2)) {
		fwrite($socket, "QUERY " . $username . ":" . $_SERVER['REMOTE_ADDR'] . ":" . $last . "\r\n");
		while (!feof($socket)) {
			echo fgets($socket, 128);
		}
		fclose($socket);
	} else {
		echo "<error>The server seems to be OFFLINE.</error>\n";
	}
}
echo "</chattercraft>\n";
?>