#! /system/bin/sh
#echo "loopback test"
receiver_to_handsetmic=7
receiver_to_speakmic=19
receiver_to_headsetmic=23
speak_to_handsetmic=17
speak_to_speakmic=8
speak_to_headsetmic=22
headphone_to_handsetmic=18
headphone_to_speakmic=20
headphone_to_headsetmic=24

enable=1
disable=0
open="-Y"
close="-N"
pname_play="agmplay"
pbname="loopbacktest"
loop_status=`getprop sys.loopback-status`
if test $2 -eq $enable
then
	loopbacktest $open "$1" 13
	if test $1 -eq $headphone_to_handsetmic -o $1 -eq $headphone_to_speakmic -o $1 -eq $headphone_to_headsetmic
	then
		start agmhostless_headphone
	elif test $1 -eq $speak_to_handsetmic -o $1 -eq $speak_to_speakmic -o $1 -eq $speak_to_headsetmic
	then
		start agmhostless_spk
	elif test $1 -eq $receiver_to_handsetmic -o $1 -eq $receiver_to_speakmic -o $1 -eq $receiver_to_headsetmic
	then
		start agmhostless_rec
	else
		#if test $1 -eq $spk -o $1 -eq $rec
		#then
		#setprop sys.agmhostless-spk 1
		#start agmhostless_spk
		#else
		#start agmhostless_loop
		echo "playback test use record-playback!"
		#fi
	fi
elif test $2 -eq $disable
then
	if test $1 -eq $headphone_to_handsetmic -o $1 -eq $headphone_to_speakmic -o $1 -eq $headphone_to_headsetmic
	then
		#pkill -f $pbname
		loopbacktest $close "$1"
		stop agmhostless_headphone
	elif test $1 -eq $speak_to_handsetmic -o $1 -eq $speak_to_speakmic -o $1 -eq $speak_to_headsetmic
	then
		#pkill -f $pbname
		loopbacktest $close "$1"
		stop agmhostless_spk
	elif test $1 -eq $receiver_to_handsetmic -o $1 -eq $receiver_to_speakmic -o $1 -eq $receiver_to_headsetmic
	then
		pkill -f $pname
		loopbacktest $close "$1"
		stop agmhostless_rec
	else
		echo "close playback paths"
	fi
else
	echo "input error ctl cmd!"
fi
