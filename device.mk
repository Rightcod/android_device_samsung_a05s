#
# Copyright (C) 2025 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from bengal-common
$(call inherit-product, device/samsung/bengal-common/bengal.mk)

# Soong namespaces
PRODUCT_SOONG_NAMESPACES += \
    $(LOCAL_PATH)

# Unified variants:
# bind-mounts per-model ipa_fws/wlanmdsp via ro.boot.em.model
# SM-A057F / SM-A057G / SM-A057M / SM-E145F / SM-M145F
# G from DYH1 stock; M/E145F/M145F real fw from UN1CA; F reuses G files
# until an F dump is available (see proprietary-files-variants.txt).
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/etc/init.a05s.unify.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/init.a05s.unify.rc

# DEBUG: mirror userspace logs into kmsg (system side, survives in last_kmsg)
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/etc/init.a05s.kmsglog.rc:$(TARGET_COPY_OUT_SYSTEM)/etc/init/init.a05s.kmsglog.rc

VENDOR_FW := vendor/samsung/bengal/proprietary/vendor/firmware
PRODUCT_COPY_FILES += \
    $(VENDOR_FW)/SM-A057F/ipa_fws.b01:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057F/ipa_fws.b01 \
    $(VENDOR_FW)/SM-A057F/ipa_fws.elf:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057F/ipa_fws.elf \
    $(VENDOR_FW)/SM-A057F/ipa_fws.mdt:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057F/ipa_fws.mdt \
    $(VENDOR_FW)/SM-A057F/wlanmdsp.mbn:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057F/wlanmdsp.mbn \
    $(VENDOR_FW)/SM-A057G/ipa_fws.b01:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057G/ipa_fws.b01 \
    $(VENDOR_FW)/SM-A057G/ipa_fws.elf:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057G/ipa_fws.elf \
    $(VENDOR_FW)/SM-A057G/ipa_fws.mdt:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057G/ipa_fws.mdt \
    $(VENDOR_FW)/SM-A057G/wlanmdsp.mbn:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057G/wlanmdsp.mbn \
    $(VENDOR_FW)/SM-A057M/ipa_fws.b01:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057M/ipa_fws.b01 \
    $(VENDOR_FW)/SM-A057M/ipa_fws.elf:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057M/ipa_fws.elf \
    $(VENDOR_FW)/SM-A057M/ipa_fws.mdt:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057M/ipa_fws.mdt \
    $(VENDOR_FW)/SM-A057M/wlanmdsp.mbn:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-A057M/wlanmdsp.mbn \
    $(VENDOR_FW)/SM-E145F/ipa_fws.b01:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-E145F/ipa_fws.b01 \
    $(VENDOR_FW)/SM-E145F/ipa_fws.elf:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-E145F/ipa_fws.elf \
    $(VENDOR_FW)/SM-E145F/ipa_fws.mdt:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-E145F/ipa_fws.mdt \
    $(VENDOR_FW)/SM-E145F/wlanmdsp.mbn:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-E145F/wlanmdsp.mbn \
    $(VENDOR_FW)/SM-M145F/ipa_fws.b01:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-M145F/ipa_fws.b01 \
    $(VENDOR_FW)/SM-M145F/ipa_fws.elf:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-M145F/ipa_fws.elf \
    $(VENDOR_FW)/SM-M145F/ipa_fws.mdt:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-M145F/ipa_fws.mdt \
    $(VENDOR_FW)/SM-M145F/wlanmdsp.mbn:$(TARGET_COPY_OUT_VENDOR)/firmware/SM-M145F/wlanmdsp.mbn

# Inherit the proprietary files
$(call inherit-product, vendor/samsung/bengal/bengal-vendor.mk)


# Mount point for /metadata inside system-as-root
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/metadata/.keep:$(TARGET_COPY_OUT_SYSTEM)/metadata/.keep

# Canonical directories for dynamic partitions
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/system_ext/.keep:$(TARGET_COPY_OUT_SYSTEM)/system_ext/.keep \
    $(LOCAL_PATH)/rootdir/product/.keep:$(TARGET_COPY_OUT_SYSTEM)/product/.keep

# Skip mounting bundled partitions in first stage init
PRODUCT_PACKAGES += \
    gsi_skip_mount.cfg
