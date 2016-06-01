SUMMARY = "A Go DNS library with a granular approach"
HOMEPAGE = "https://github.com/miekg/dns"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=147353de6868a20caa562d26eab7b3c5"

PKG_NAME = "github.com/miekg/dns"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "7864d445e5087e8d761dbefec43f29b92f7650eb"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "dns_sysroot_preprocess"

dns_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
