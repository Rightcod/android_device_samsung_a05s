#!/usr/bin/env -S PYTHONPATH=../../../tools/extract-utils python3
#
# Unified Samsung Galaxy A05s (a05s) - base DYH1 (OneUI 7 / Android 15).
# Covers SM-A057F / SM-A057G / SM-A057M / SM-E145F / SM-M145F.
#
# SPDX-License-Identifier: Apache-2.0
#

from extract_utils.main import (
    ExtractUtils,
    ExtractUtilsModule,
)
from extract_utils.fixups_blob import (
    blob_fixup,
    blob_fixups_user_type,
)
from extract_utils.fixups_lib import (
    lib_fixup_vendorcompat,
    lib_fixups_user_type,
    libs_proto_3_9_1,
)

blob_fixups: blob_fixups_user_type = {
    # QCOM RIL: use the vendor radio device prop (needed for RIL/SMS).
    'vendor/lib64/libril-qc-hal-qmi.so': blob_fixup()
        .binary_regex_replace(b'ro.product.vendor.device', b'ro.vendor.radio.midevice'),
    'vendor/lib/libril-qc-hal-qmi.so': blob_fixup()
        .binary_regex_replace(b'ro.product.vendor.device', b'ro.vendor.radio.midevice'),
}  # fmt: skip


def lib_fixup_hyper_vendor(lib: str, *args, **kwargs):
    # libhyper collides with the Rust hyper crate module name; suffix ours.
    # Installed filename is preserved via stem (see tools/fixup-vendor-bp.sh).
    return f'{lib}_vendor'


lib_fixups: lib_fixups_user_type = {
    (
        'libhyper',
        'libhypervintf',
    ): lib_fixup_hyper_vendor,
    libs_proto_3_9_1: lib_fixup_vendorcompat,
}

module = ExtractUtilsModule(
    'bengal',
    'samsung',
    device_rel_path='device/samsung/a05s',
    blob_fixups=blob_fixups,
    lib_fixups=lib_fixups,
)

if __name__ == '__main__':
    utils = ExtractUtils.device(module)
    utils.run()
