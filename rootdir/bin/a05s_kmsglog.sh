#!/system/bin/sh
# Stream logcat directly to /dev/kmsg
/system/bin/logcat -b main,system,crash -v threadtime > /dev/kmsg
