#
# Copyright (C) 2024 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# Inherit from qssi device
$(call inherit-product, device/samsung/a05s/device.mk)

PRODUCT_DEVICE := a05s
PRODUCT_NAME := lineage_a05s
PRODUCT_BRAND := samsung
PRODUCT_MODEL := SM-A057G
PRODUCT_MANUFACTURER := samsung

PRODUCT_GMS_CLIENTID_BASE := android-samsung-ss
BUILD_FINGERPRINT := samsung/a05sxx/qssi:15/AP3A.240905.015.A2/A057GXXS8DYH1:user/release-keys
