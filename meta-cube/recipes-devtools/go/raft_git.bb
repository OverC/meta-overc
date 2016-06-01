SUMMARY = "Go library for providing consensus"
HOMEPAGE = "https://github.com/hashicorp/raft"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS = "go-metrics go-msgpack"
CLEANBROKEN = "1"

PKG_NAME = "github.com/hashicorp/raft"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "379e28eb5a538707eae7a97ecc60846821217f27"

S = "${WORKDIR}/git"

#Custom do_compile to stop go from trying to autotest and fetch deps.
do_compile() {
}

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "raft_sysroot_preprocess"

raft_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
