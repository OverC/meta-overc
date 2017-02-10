SUMMARY = "Simple mDNS client/server library in Golang"
HOMEPAGE = "https://github.com/hashicorp/mdns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb04212e101fbbd028f325e04ad45778"

DEPENDS += "go-cross-${TARGET_ARCH} dns"

PKG_NAME = "github.com/hashicorp/mdns"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "2b439d37011456df8ff83a70ffd1cd6046410113"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    install -m 0644 ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "mdns_sysroot_preprocess"

mdns_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
