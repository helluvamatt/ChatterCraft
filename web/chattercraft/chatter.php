<?PHP
header("Content-type: text/xhtml; charset=UTF-8");

//TODO Handle timeouts for when the server is offline.

$query_server = "localhost";
$port = 25566;

$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : "ACTION_QUERY";
echo "<?xml version=\"1.0\">\n";
echo "<chattercraft>\n";
if ($action == "ACTION_CHATTER") {
	if (isset($_REQUEST['user']) && ($user = $_REQUEST['user']) != "") {
		if (isset($_REQUEST['msg']) && ($message = $_REQUEST['msg']) != "") {
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
		echo "<error>Cannot send a message without being logged in.</error>\n";
	}
} else if ($action == "ACTION_LOGIN") {
	$username = isset($_REQUEST['user']) ? $_REQUEST['user'] : "";
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
	echo "<!-- Default ACTION -->\n";
	$username = isset($_REQUEST['user']) ? $_REQUEST['user'] : "";
	$last = isset($_REQUEST['last']) ? filter_var($_REQUEST['last'], FILTER_SANITIZE_NUMBER_INT) : 0;
	// Connect and query
	if ($socket = fsockopen($query_server, $port, $errno, $error, 2)) {
		fwrite($socket, "QUERY_XML " . $username . ":" . $_SERVER['REMOTE_ADDR'] . ":" . $last . "\r\n");
		while (!feof($socket)) {
			echo fgets($socket, 128);
		}
		fclose($socket);
	} else {
		echo "<error>The server seems to be OFFLINE.</error>\n";
	}
	echo "<!-- End Default ACTION -->\n";
}
echo "</chattercraft>\n";
?>
