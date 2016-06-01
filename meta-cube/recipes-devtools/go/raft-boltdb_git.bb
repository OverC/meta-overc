SUMMARY = "Go package that exports the BoltStore"
HOMEPAGE = "https://github.com/hashicorp/raft-boltdb"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2dd1a9ecf92cd5617f128808f9b85b44"

DEPENDS = "bolt raft go-msgpack"

PKG_NAME = "github.com/hashicorp/raft-boltdb"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "d1e82c1ec3f15ee991f7cc7ffd5b67ff6f5bbaee"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "raft_boltdb_sysroot_preprocess"

raft_boltdb_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
