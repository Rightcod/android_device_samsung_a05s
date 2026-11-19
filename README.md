# Android device tree for Samsung Galaxy A05s (SM-A057F/G/M, SM-E145F, SM-M145F)

LineageOS 23.2 (Android 16) standalone unified device tree.  
Base firmware: `A057GXXS8DYH1` (Snapdragon 680 / Qualcomm Bengal `SM6225`).  
Dynamic model unification handled via `init.qcom.sh`.

---

## Applying Platform Patches
This tree includes platform patches for LineageOS 23.2 (game FPS unlock spoofing, SurfaceFlinger HWC wait, BPF loader fallbacks, and build system fixes).

To apply all patches to your LineageOS tree:
```bash
./device/samsung/a05s/apply-patches.sh
```

Included patches in `patches/`:
- `frameworks_base.patch`: S24 Ultra game spoofing (Free Fire, BGMI, PUBG Mobile, CoD, Standoff 2, etc.) for high FPS unlock, Google Photos unlimited backup, SignalDrawable fallback, DisplayDeviceConfig auto-brightness clamp.
- `frameworks_native.patch`: SurfaceFlinger HidlComposer retry wait loop for late hwcomposer bringup.
- `system_core.patch`: Canonical mountpoint symlink-to-directory fix, switch_root fallback, rootdir structure creation.
- `system_bpf.patch` & `packages_modules_Connectivity.patch`: Non-fatal fallback for legacy 4.19 / 5.4 kernel BPF objects.
- `packages_services_Mtp.patch`: Notification channel fallback for MTP.
- `packages_modules_UprobeStats.patch`: Fix min_sdk_version for Rust uprobestats.
- `packages_apps_CarrierConfig.patch`: Additional carrier configuration profiles.
- `build_make.patch` & `build_soong.patch`: OTA releasetools and custom `SOONG_TEMPDIR` support.
- `device_qcom_sepolicy_vndr_sm6225.patch`: Qualcomm vendor sepolicy cleanup.

---

## Building LineageOS 23.2 for Galaxy A05s
```bash
# Initialize build environment
source build/envsetup.sh

# Apply device platform patches
./device/samsung/a05s/apply-patches.sh

# Select target
lunch lineage_a05s-bp2a-userdebug

# Build flashable ZIP
m bacon -j$(nproc)
```

---

## Contributors & Credits
- **Rightcode** ([@Rightcod](https://github.com/Rightcod))
- **The LineageOS Project**

```
#
# Copyright (C) 2024-2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#
```
