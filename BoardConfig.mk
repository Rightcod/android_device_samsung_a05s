DEVICE_PATH := device/samsung/a05s

# Unified: all models except ancient bootloaders (AWI7/AWK5 blocked in installer/check_bl.sh)
TARGET_OTA_ASSERT_DEVICE := a05s,SM-A057F,SM-A057G,SM-A057M,SM-E145F,SM-M145F

# Kernel - prebuilt (from A057GXXS8DYH1, OneUI 7 / Android 15, 5.15, SM6225)
# NOTE: kernel source at kernel/samsung/a05s exists for UAPI headers only
# (generated_kernel_includes); the binary is prebuilt, gki_defconfig just
# satisfies the kernel.mk config check, no kernel is compiled.
TARGET_FORCE_PREBUILT_KERNEL := true
TARGET_PREBUILT_KERNEL := $(DEVICE_PATH)/prebuilts/kernel
TARGET_KERNEL_CONFIG := gki_defconfig
# 23.2 wants a dir of .dtb files (concatenated at build); split from stock
BOARD_PREBUILT_DTBIMAGE_DIR := $(DEVICE_PATH)/prebuilts/dtb
BOARD_PREBUILT_DTBOIMAGE := $(DEVICE_PATH)/prebuilts/dtbo.img

# Inherit from common tree
include device/samsung/bengal-common/BoardConfigCommon.mk

# Sepolicy (unified firmware file_contexts)
BOARD_VENDOR_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/vendor
