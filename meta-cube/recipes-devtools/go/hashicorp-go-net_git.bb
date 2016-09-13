SUMMARY = "Supplementary Go networking libraries"
HOMEPAGE = "https://github.com/hashicorp/go.net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PKG_NAME = "github.com/hashicorp/go.net"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "cbad13bf000d0cbdbc71506b26e94bcc72bbe74d"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_go_net_sysroot_preprocess"

hashicorp_go_net_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
