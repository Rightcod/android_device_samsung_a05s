#
# Copyright (C) 2024-2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Add common definitions for Qualcomm
$(call inherit-product, hardware/qcom-caf/common/common.mk)

# Soong namespaces
PRODUCT_SOONG_NAMESPACES += \
    $(LOCAL_PATH)

PRODUCT_PACKAGES += \
    update_engine \
    update_engine_sideload \
    update_verifier

PRODUCT_PACKAGES += \
    checkpoint_gc \
    otapreopt_script

# AAPT
PRODUCT_AAPT_CONFIG := normal
PRODUCT_AAPT_PREF_CONFIG := xxhdpi

# Audio
PRODUCT_PACKAGES += \
    audio.bluetooth_qti.default \
    audio.primary.bengal \
    audio.r_submix.default \
    audio.usb.default \
    android.hardware.audio@7.1-impl \
    android.hardware.audio.effect@7.0-impl \
    android.hardware.audio.service \
    android.hardware.bluetooth.audio@2.0-impl \
    android.hardware.soundtrigger@2.3-impl \
    libaudiopreprocessing \
    libbundlewrapper \
    libdownmix \
    libdynproc \
    libeffectproxy \
    libldnhncr \
    libqcompostprocbundle \
    libqcomvisualizer \
    libqcomvoiceprocessing \
    libreverbwrapper \
    libvisualizer \
    libvolumelistener

# Audio - Configs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/audio/audio_effects.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio_effects.xml \
    $(LOCAL_PATH)/audio/audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/bluetooth_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/bluetooth_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/r_submix_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/r_submix_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/usb_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/usb_audio_policy_configuration.xml

# Bluetooth
PRODUCT_PACKAGES += \
    android.hardware.bluetooth@1.0.vendor \
    vendor.qti.hardware.bluetooth_audio@2.1.vendor \
    vendor.qti.hardware.btconfigstore@1.0.vendor \
    vendor.qti.hardware.btconfigstore@2.0.vendor

# Camera
PRODUCT_PACKAGES += \
    android.hardware.camera.provider@2.4-impl \
    android.hardware.camera.provider@2.4-service_64 \
    libcamera2ndk_vendor

# Charger
PRODUCT_PACKAGES += \
    charger_res_images_vendor

# Component overrides
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/component-overrides.xml:$(TARGET_COPY_OUT_VENDOR)/etc/sysconfig/component-overrides.xml

# Display
PRODUCT_PACKAGES += \
    libdisplayconfig.qti \
    libqdMetaData \
    libsdmcore \
    libsdmutils \
    libtinyxml \
    vendor.qti.hardware.display.allocator-service \
    vendor.qti.hardware.display.composer-service

# DRM
PRODUCT_PACKAGES += \
    android.hardware.drm-service.clearkey

# Fastboot
PRODUCT_PACKAGES += \
    fastbootd

# HIDL
PRODUCT_PACKAGES += \
    android.hidl.base@1.0 \
    android.hidl.base@1.0.vendor \
    android.hidl.manager@1.0 \
    android.hidl.manager@1.0.vendor \
    libhidltransport \
    libhidltransport.vendor \
    libhwbinder \
    libhwbinder.vendor

# Lineage Health
PRODUCT_PACKAGES += \
    vendor.lineage.health-service.default

# Media
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/media/media_codecs.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs.xml \
    $(LOCAL_PATH)/media/media_codecs_performance_khaje_v0.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_performance.xml \
    $(LOCAL_PATH)/media/media_codecs_vendor_audio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_vendor_audio.xml \
    $(LOCAL_PATH)/media/media_profiles.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles_vendor.xml \
    $(LOCAL_PATH)/media/media_profiles_khaje_v0.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles.xml \
    $(LOCAL_PATH)/media/media_profiles_V1_0.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles_V1_0.xml

# NFC
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/nfc/libnfc-sec-vendor.conf:$(TARGET_COPY_OUT_VENDOR)/etc/libnfc-sec-vendor.conf

# Overlays
DEVICE_PACKAGE_OVERLAYS += \
    $(LOCAL_PATH)/overlay \
    $(LOCAL_PATH)/overlay-lineage

# Permissions
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.audio.low_latency.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.audio.low_latency.xml \
    frameworks/native/data/etc/android.hardware.bluetooth.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth.xml \
    frameworks/native/data/etc/android.hardware.bluetooth_le.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth_le.xml \
    frameworks/native/data/etc/android.hardware.camera.flash-autofocus.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.flash-autofocus.xml \
    frameworks/native/data/etc/android.hardware.camera.front.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.front.xml \
    frameworks/native/data/etc/android.hardware.camera.full.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.full.xml \
    frameworks/native/data/etc/android.hardware.camera.raw.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.raw.xml \
    frameworks/native/data/etc/android.hardware.fingerprint.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.fingerprint.xml \
    frameworks/native/data/etc/android.hardware.location.gps.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.location.gps.xml \
    frameworks/native/data/etc/android.hardware.opengles.aep.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.opengles.aep.xml \
    frameworks/native/data/etc/android.hardware.sensor.accelerometer.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.accelerometer.xml \
    frameworks/native/data/etc/android.hardware.sensor.compass.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.compass.xml \
    frameworks/native/data/etc/android.hardware.sensor.light.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.light.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepcounter.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepcounter.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepdetector.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepdetector.xml \
    frameworks/native/data/etc/android.hardware.telephony.gsm.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.telephony.gsm.xml \
    frameworks/native/data/etc/android.hardware.telephony.ims.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.telephony.ims.xml \
    frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml \
    frameworks/native/data/etc/android.hardware.usb.accessory.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.usb.accessory.xml \
    frameworks/native/data/etc/android.hardware.usb.host.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.usb.host.xml \
    frameworks/native/data/etc/android.hardware.vulkan.compute-0.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.compute-0.xml \
    frameworks/native/data/etc/android.hardware.vulkan.level-1.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.level-1.xml \
    frameworks/native/data/etc/android.hardware.vulkan.version-1_1.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.version-1_1.xml \
    frameworks/native/data/etc/android.hardware.wifi.direct.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.direct.xml \
    frameworks/native/data/etc/android.hardware.wifi.passpoint.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.passpoint.xml \
    frameworks/native/data/etc/android.hardware.wifi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.xml \
    frameworks/native/data/etc/android.software.ipsec_tunnels.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.ipsec_tunnels.xml \
    frameworks/native/data/etc/android.software.midi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.midi.xml \
    frameworks/native/data/etc/android.software.opengles.deqp.level-2021-03-01.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.opengles.deqp.level.xml \
    frameworks/native/data/etc/android.software.sip.voip.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.sip.voip.xml \
    frameworks/native/data/etc/android.software.vulkan.deqp.level-2021-03-01.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.vulkan.deqp.level.xml

# Power
PRODUCT_PACKAGES += \
    android.hardware.power-service-qti

# Public libraries
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/linker/public.libraries.txt:$(TARGET_COPY_OUT_VENDOR)/etc/public.libraries.txt \
    $(LOCAL_PATH)/configs/linker/public.libraries-qti.txt:$(TARGET_COPY_OUT_VENDOR)/etc/public.libraries-qti.txt

# QTI service
PRODUCT_PACKAGES += \
    libqti_vndfwk_detect.vendor

# Rootdir scripts & init configs
PRODUCT_PACKAGES += \
    init.class_main.sh \
    init.crda.sh \
    init.kernel.post_boot.sh \
    init.kernel.post_boot-bengal.sh \
    init.mdm.sh \
    init.qcom.class_core.sh \
    init.qcom.coex.sh \
    init.qcom.early_boot.sh \
    init.qcom.efs.sync.sh \
    init.qcom.post_boot.sh \
    init.qcom.sdio.sh \
    init.qcom.sensors.sh \
    init.qcom.sh \
    init.qti.chg_policy.sh \
    init.qti.dcvs.sh \
    init.qti.display_boot.sh \
    init.qti.early_init.sh \
    init.qti.kernel.debug-bengal.sh \
    init.qti.kernel.debug.sh \
    init.qti.kernel.sh \
    init.qti.media.sh \
    init.qti.qcv.sh \
    init.qti.write.sh \
    install-recovery.sh \
    qca6234-service.sh \
    vendor_modprobe.sh

PRODUCT_PACKAGES += \
    init.a05s.rc \
    init.qcom.factory.rc \
    init.qcom.rc \
    init.qti.kernel.rc \
    init.qti.ufs.rc \
    init.recovery.qcom.rc \
    init.recovery.samsung.rc \
    init.samsung.bsp.rc \
    init.samsung.rc \
    init.target.rc \
    fstab.ramplus \
    IPACM_cfg.xml

# USB
PRODUCT_PACKAGES += \
    android.hardware.usb-service.qti \
    android.hardware.usb.gadget-service.qti

# Copy usb rc and sh via PRODUCT_COPY_FILES
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/init/init.qcom.usb.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.qcom.usb.rc \
    $(LOCAL_PATH)/rootdir/bin/init.qcom.usb.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.qcom.usb.sh

# Vendor service manager
PRODUCT_PACKAGES += \
    vndservicemanager

# Vibrator
PRODUCT_PACKAGES += \
    vendor.samsung.hardware.vibrator-service

# Wifi
PRODUCT_PACKAGES += \
    vendor.samsung.hardware.wifi@2.0-service \
    libwpa_client \
    libwifi-hal-qcom \
    wpa_supplicant

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/wifi/p2p_supplicant_overlay.conf:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/p2p_supplicant_overlay.conf \
    $(LOCAL_PATH)/wifi/WCNSS_qcom_cfg.ini:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/WCNSS_qcom_cfg.ini \
    $(LOCAL_PATH)/wifi/wpa_supplicant_overlay.conf:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/wpa_supplicant_overlay.conf

# Unified variants firmware:
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

# DEBUG: mirror userspace logs into kmsg
PRODUCT_PACKAGES += \
    init.a05s.kmsglog.rc

# Stock vendor SELinux policies from DYH1
PRODUCT_PACKAGES += \
    stock_vendor_file_contexts \
    stock_vendor_property_contexts \
    stock_vendor_hwservice_contexts \
    stock_vendor_service_contexts

# Inherit proprietary blobs
$(call inherit-product, vendor/samsung/bengal/bengal-vendor.mk)
