<?php
/**
 * icon.php - Generate icons for player and
 */

header('Content-type: image/png');

$player = isset($_REQUEST['player']) ? $_REQUEST['player'] : null;
$usage =  isset($_REQUEST['usage'])  ? $_REQUEST['usage']  : 'marker';

function create_blank($w, $h)
{
	$im = imagecreatetruecolor($w, $h);
	imagesavealpha($im, true);
	$transparent = imagecolorallocatealpha($im, 0, 0, 0, 127);
	imagefill($im, 0, 0, $transparent);
	return $im;
}

function load_player_skin($imgname)
{
	/* Attempt to open */
	if ($imgname != null) {
		$im = @imagecreatefrompng($imgname);
	}

	/* See if it failed, use default if it did */
	if(!$im) {
		$im = imagecreatefrompng('./char.png');
	}

	return $im;
}

$img = load_player_skin('http://www.minecraft.net/skin/'.$player.'.png');

if($usage == 'list')
{
	$myhead = imagecreate(15, 15);
	$color = imagecolorallocate($myhead, 250, 250, 250);
	imagefill($myhead, 0, 0, $color);
	imagecopyresized($myhead, $img, 1, 1, 8, 8, 13, 13, 8, 8);
}

if($usage == 'marker')
{
	$myhead = create_blank(32, 37);
	$mymarker = imagecreatefrompng('./marker_blank.png');
	imagecopy($myhead, $mymarker, 0, 0, 0, 0, 32, 37);
	imagecopyresized($myhead, $img, 8, 8, 8, 8, 16, 16, 8, 8);
}

if($usage == 'info')
{
	$myhead = create_blank(48, 96);
	imagecopyresized($myhead, $img, 12,0,8,8,24,24,8,8);
	imagecopyresized($myhead, $img, 12,24,20,20,24,26,8,12);
	imagecopyresized($myhead, $img, 12,50,0,20,12,26,4,12);
	imagecopyresized($myhead, $img, 24,50,8,20,12,26,4,12);
	imagecopyresized($myhead, $img, 2,24,44,20,10,26,4,12);
	imagecopyresized($myhead, $img, 36,24,52,20,10,26,4,12);
	imagecopyresized($myhead, $img, 6,6,32,10,6,3,2,1);
	imagecopyresized($myhead, $img, 36,6,32,10,6,3,2,1);
}

imagepng($myhead);
imagedestroy($img);
imagedestroy($myhead);
?>
