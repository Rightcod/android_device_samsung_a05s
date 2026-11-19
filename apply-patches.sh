#!/bin/bash
#
# Script to apply all ROM and framework patches required for Samsung Galaxy A05s (SM-A057F/G/M)
# on LineageOS 23.2 (Android 16).
#
# Copyright (C) 2024-2026 Rightcode (@Rightcod)
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOP="${ANDROID_BUILD_TOP:-$(cd "${SCRIPT_DIR}/../../.." && pwd)}"

echo "==> Android build top: ${TOP}"
echo "==> Applying Galaxy A05s platform patches..."

apply_patch() {
    local target_dir="$1"
    local patch_file="$2"
    local full_target="${TOP}/${target_dir}"
    local full_patch="${SCRIPT_DIR}/patches/${patch_file}"

    if [ ! -d "${full_target}" ]; then
        echo "[-] Skipping ${target_dir} (directory not found)"
        return 0
    fi

    if [ ! -f "${full_patch}" ]; then
        echo "[-] Skipping ${patch_file} (patch file not found)"
        return 0
    fi

    echo "[+] Applying ${patch_file} -> ${target_dir}"
    if git -C "${full_target}" apply --check "${full_patch}" >/dev/null 2>&1; then
        git -C "${full_target}" apply "${full_patch}"
        echo "    Successfully applied ${patch_file}"
    elif git -C "${full_target}" apply -R --check "${full_patch}" >/dev/null 2>&1; then
        echo "    Patch ${patch_file} already applied, skipping"
    else
        echo "    [WARNING] Conflicts detected while applying ${patch_file}; trying with 3-way merge"
        git -C "${full_target}" apply --3way "${full_patch}" || {
            echo "    [ERROR] Failed to apply ${patch_file} to ${target_dir}"
            return 1
        }
    fi
}

# 1. Frameworks
apply_patch "frameworks/base" "frameworks_base.patch"
apply_patch "frameworks/native" "frameworks_native.patch"

# 2. System Core & BPF
apply_patch "system/core" "system_core.patch"
apply_patch "system/bpf" "system_bpf.patch"

# 3. Packages & Modules
apply_patch "packages/modules/Connectivity" "packages_modules_Connectivity.patch"
apply_patch "packages/modules/UprobeStats" "packages_modules_UprobeStats.patch"
apply_patch "packages/services/Mtp" "packages_services_Mtp.patch"
apply_patch "packages/apps/CarrierConfig" "packages_apps_CarrierConfig.patch"

# 4. Build system
apply_patch "build/make" "build_make.patch"
apply_patch "build/soong" "build_soong.patch"

# 5. Qualcomm HAL Sepolicy
apply_patch "device/qcom/sepolicy_vndr/sm6225" "device_qcom_sepolicy_vndr_sm6225.patch"

echo "==> All Galaxy A05s patches applied successfully!"
