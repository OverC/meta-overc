SUMMARY = "HIL is a small embedded language for string interpolations."
HOMEPAGE = "https://github.com/hashicorp/hil"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

DEPENDS += "go-net"

PKG_NAME = "github.com/hashicorp/hil"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "4cc4a6ebbc3a6ecd7d8f1e6fcd75cf52096e6271"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_hil_sysroot_preprocess"

hashicorp_hil_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
