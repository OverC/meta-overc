SUMMARY = "Go package that exports the MDBStore"
HOMEPAGE = "https://github.com/hashicorp/raft-mdb"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2dd1a9ecf92cd5617f128808f9b85b44"

DEPENDS += "gomdb"

PKG_NAME = "github.com/hashicorp/raft-mdb"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "4ec3694ffbc74d34f7532e70ef2e9c3546a0c0b0"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    install -m 0644 ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "raft_mdb_sysroot_preprocess"

raft_mdb_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
