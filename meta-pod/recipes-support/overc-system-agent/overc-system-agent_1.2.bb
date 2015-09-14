SUMMARY = "OverC host update system"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = " \
    file://${BPN}-${PV}/* \
"

inherit distutils

RDEPENDS_${PN} = "btrfs-tools python-smartpm python-argparse python-subprocess"

FILES_${PN}-dev += "${libdir}/pkgconfig"

