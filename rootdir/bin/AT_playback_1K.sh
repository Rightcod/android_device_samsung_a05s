#! /system/bin/sh
#echo "Playback test"
#spk=1
#rec=2
#mic1=3
#mic2=4
playback=1
playback_rcv=2
enable=1
disable=0
open="-Y"
close="-N"
pname_play="agmplay"
pbname="loopbacktest"

if test $2 -eq $enable
then
	loopbacktest $open "$1" 13
	if test $1 -eq $playback
	then
		setenforce 0
		agmplay /vendor/etc/MP3_1KHZ_L.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-1
		#pkill -f $pbname
		pkill -f $pname_play
		loopbacktest $close "$1"
	elif test $1 -eq $playback_rcv
	then
		setenforce 0
		agmplay /vendor/etc/MP3_1KHZ_L.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		pkill -f $pname_play
		loopbacktest $close "$1"
	fi
elif test $2 -eq $disable
then
	if test $1 -eq $playback -o $1 -eq $playback_rcv
	then
	setenforce 0
	#pkill -f $pbname
	pkill -f $pname_play
	loopbacktest $close "$1"
	else
	setenforce 0
	loopbacktest $close "$1"
	fi
else
	echo "input error ctl cmd!"
fi

