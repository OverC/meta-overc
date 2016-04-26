SUMMARY = "Generates UUID-format strings using purely high quality random bytes"
HOMEPAGE = "https://github.com/hashicorp/go-uuid"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

PKG_NAME = "github.com/hashicorp/go-uuid"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "73d19cdc2bf00788cc25f7d5fd74347d48ada9ac"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_uuid_sysroot_preprocess"

hashicorp_uuid_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
