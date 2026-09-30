#! /system/bin/sh
#echo "Playback test"
handsetmic_to_receiver=15
handsetmic_to_speak=13
handsetmic_headphone=3

speakmic_to_receiver=16
speakmic_to_speak=14
speakmic_to_headphone=4

headsetmic_to_receiver=6
headsetmic_to_speak=5
headsetmic_to_headphone=12

enable=1
disable=0
open="-Y"
close="-N"
pname_play="agmplay"
pname_cap="agmcap"
pbname="loopbacktest"

if test $2 -eq $enable
then
	loopbacktest $open "$1" 13
	if test $1 -eq $handsetmic_to_receiver
	then
		setenforce 0
		agmplay /vendor/etc/rcv_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		agmcap /data/handsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq  $speakmic_to_receiver
	then
		setenforce 0
		agmplay /vendor/etc/rcv_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		agmcap /data/speakmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $headsetmic_to_receiver
	then
		setenforce 0
		agmplay /vendor/etc/rcv_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		agmcap /data/headsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $handsetmic_to_speak
	then
		setenforce 0
		agmplay /vendor/etc/spk_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-1
		agmcap /data/handsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $speakmic_to_speak
	then
		setenforce 0
		agmplay /vendor/etc/spk_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-1
		agmcap /data/speakmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $headsetmic_to_speak
	then
		setenforce 0
		agmplay /vendor/etc/spk_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-1
		agmcap /data/headsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $handsetmic_headphone
	then
		setenforce 0
		agmplay /vendor/etc/rcv_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		agmcap /data/handsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $speakmic_to_headphone
	then
		setenforce 0
		agmplay /vendor/etc/rcv_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-0
		agmcap /data/speakmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $headsetmic_to_headphone
	then
		setenforce 0
		agmplay /vendor/etc/spk_pb.wav –D 0 -d 100 -i CODEC_DMA-LPAIF_RXTX-RX-1
		agmcap /data/headsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	else
		echo "input error ctl cmd"
	fi
elif test $2 -eq $disable
then
	if test $1 -eq $handsetmic_to_receiver$1
	then
		#pkill -f $pbname
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $speakmic_to_receiver
	then
		#pkill -f $pbname
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $headsetmic_to_receiver
	then
		#pkill -f $pbname
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $handsetmic_to_speak
	then
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $speakmic_to_speak
	then
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test -eq $headsetmic_to_speak
	then
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $handsetmic_headphone
	then
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $speakmic_to_headphone
	then
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	elif test $1 -eq $headsetmic_to_headphone
	then 
		pkill -f $pname_play
		pkill -f $pname_cap
		loopbacktest $close "$1"
	else
		loopbacktest $close "$1"
	fi
else
	echo "input error ctl cmd!"
fi
