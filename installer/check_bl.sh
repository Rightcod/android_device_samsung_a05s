#!/bin/sh
# Block only ancient bootloaders (AWI7 / AWK5 and older). Everything else passes.
# Any model: SM-A057F / SM-A057G / SM-A057M / SM-E145F / SM-M145F
BL=$(getprop ro.boot.bootloader 2>/dev/null)
case "$BL" in
  *AWI7*|*AWK5*|*AWI[0-6]*|*AWK[0-4]*)
    echo "Ancient bootloader $BL not supported. Flash BL+CP from DYH1 via Odin first."
    exit 1
    ;;
esac
exit 0
