#
# Copyright (C) 2024-2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := stock_vendor_file_contexts
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR)/etc/selinux
LOCAL_SRC_FILES := stock/vendor_file_contexts
LOCAL_MODULE_STEM := vendor_file_contexts
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := stock_vendor_property_contexts
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR)/etc/selinux
LOCAL_SRC_FILES := stock/vendor_property_contexts
LOCAL_MODULE_STEM := vendor_property_contexts
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := stock_vendor_hwservice_contexts
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR)/etc/selinux
LOCAL_SRC_FILES := stock/vendor_hwservice_contexts
LOCAL_MODULE_STEM := vendor_hwservice_contexts
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := stock_vendor_service_contexts
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR)/etc/selinux
LOCAL_SRC_FILES := stock/vendor_service_contexts
LOCAL_MODULE_STEM := vendor_service_contexts
include $(BUILD_PREBUILT)
