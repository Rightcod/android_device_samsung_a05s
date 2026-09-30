#! /system/bin/sh
#echo "record test"
handsetmic_record=9
speakermic_record=10
headsetmic_record=11
enable=1
disable=0
open="-Y"
close="-N"
pname_record="agmcap"
pbname="loopbacktest"


if test $2 -eq $enable
then
	loopbacktest $open "$1" 13
	if test $1 -eq $handsetmic_record
	then
		echo "handsetmic_record test!"
		setenforce 0
		agmcap /data/handsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $handsetmic_record
		loopbacktest $close "$1"
	elif test $1 -eq $speakermic_record
	then
		echo "handsetmic_record test!"
		setenforce 0
		agmcap /data/speakermiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $speakermic_record
		loopbacktest $close "$1"
	elif test  $1 -eq $headsetmic_record
	then
		echo "handsetmic_record test!"
		setenforce 0
		agmcap /data/headsetmiccapture.wav -D 100 -d 101 -c 1 -r 48000 -i CODEC_DMA-LPAIF_RXTX-TX-3
		pkill -f $headsetmic_record
		loopbacktest $close "$1"	
	fi
elif test $2 -eq $disable
then
	if test $1 -eq $handsetmic_record -o $1 -eq $speakermic_record -o $1 -eq $headsetmic_record
	then
	setenforce 0
	pkill -f $pbname
	loopbacktest $close "$1"
	#pkill -f $pbname
	else
	#pkill -f $pname_record
	setenforce 0
	loopbacktest $close "$1"
	#pkill -f $pbname
	fi
else
	echo "input error ctl cmd!"
fi

